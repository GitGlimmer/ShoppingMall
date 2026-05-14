package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductDTO;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.Brand;
import com.glimmer.shopping.shoppingmall.entity.Category;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.repository.BrandRepository;
import com.glimmer.shopping.shoppingmall.repository.CategoryRepository;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.service.ProductBatchImportService;
import com.glimmer.shopping.shoppingmall.util.ExcelUtil;
import com.glimmer.shopping.shoppingmall.util.FileUtil;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product/manage")
@Api(tags = "商品管理-基础操作")
public class ProductManageController {

    private static final String IMAGE_BASE_URL = "/api/product/manage/image/";

    private final ProductService productService;

    public ProductManageController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation("新增商品")
    @PostMapping
    public Result<String> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @ApiOperation("删除商品")
    @PostMapping("/delete")
    public Result<String> deleteProduct(@RequestBody ProductQueryRequest request) {
        return productService.deleteProduct(request);
    }

    @ApiOperation("修改商品")
    @PutMapping
    public Result<String> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @ApiOperation("获取商品详情")
    @PostMapping("/detail")
    public Result<ProductDTO> getProduct(@RequestBody ProductQueryRequest request) {
        return productService.getProductDetail(request);
    }

    @ApiOperation("分页查询商品列表")
    @PostMapping("/list")
    public Result<PageResult<ProductDTO>> getProductList(@RequestBody(required = false) ProductQueryRequest request) {
        if (request == null) {
            request = new ProductQueryRequest();
        }
        return productService.getProductsByPage(request);
    }

    @ApiOperation("更新商品状态（上架/下架）")
    @PostMapping("/status")
    public Result<String> updateProductStatus(@RequestBody ProductQueryRequest request) {
        return productService.updateProductStatus(request);
    }

    @ApiOperation("批量更新商品状态")
    @PostMapping("/batch/status")
    public Result<Integer> batchUpdateProductStatus(@RequestBody Map<String, Object> request) {
        List<String> ids = (List<String>) request.get("ids");
        Object status = request.get("status");
        return productService.batchUpdateProductStatus(ids, status);
    }

    @ApiOperation("图片上传")
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = FileUtil.uploadImage(file, IMAGE_BASE_URL);
            return Result.success(imageUrl);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (RuntimeException e) {
            return Result.error("图片上传失败");
        }
    }

    @ApiOperation("获取图片")
    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        return FileUtil.getImage(fileName);
    }

    @Autowired
    private ProductBatchImportService productBatchImportService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @ApiOperation("批量导入商品")
    @PostMapping("/batch/upload")
    public Result<Map<String, Object>> batchUploadProducts(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "importMode", required = false, defaultValue = "strict") String importMode,
            @RequestParam(value = "brandName", required = false) String brandName,
            @RequestParam(value = "categoryName", required = false) String categoryName) {
        if (!FileUtil.isValidExcelFile(file)) {
            return Result.error("请选择有效的Excel文件（.xlsx或.xls）");
        }
        return productBatchImportService.batchImport(file, importMode, brandName, categoryName);
    }

    @ApiOperation("获取所有品牌列表")
    @GetMapping("/brands")
    public Result<List<Map<String, String>>> getAllBrands() {
        return productBatchImportService.getAllBrands();
    }

    @ApiOperation("获取所有分类列表")
    @GetMapping("/categories")
    public Result<List<Map<String, String>>> getAllCategories() {
        return productBatchImportService.getAllCategories();
    }

    @ApiOperation("下载导入模板")
    @GetMapping("/template/download")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            String[] headers = {
                "商品名称", "分类(名称或ID)", "品牌(名称或ID)", "价格", "库存",
                "描述", "规格", "产地", "单位", "标签", "状态(0禁用/1启用)"
            };

            String[] exampleData = {
                "示例商品", "电子产品", "示例品牌", "99.99", "100",
                "这是一个示例商品描述", "规格说明", "中国", "件", "热门,新品", "1"
            };

            byte[] templateBytes = ExcelUtil.createTemplate(headers, exampleData);
            
            String fileName = "批量添加商品模版_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"))
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(templateBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("下载品牌列表")
    @GetMapping("/brands/download")
    public ResponseEntity<byte[]> downloadBrandList() {
        try {
            List<Brand> brands = brandRepository.findAll();
            String[] headers = {"品牌ID", "品牌名称"};
            List<String[]> data = new ArrayList<>();
            for (Brand brand : brands) {
                data.add(new String[]{brand.getId(), brand.getName()});
            }
            
            byte[] templateBytes = ExcelUtil.createDataExcel(headers, data);
            String fileName = "品牌列表_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"))
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(templateBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("下载分类列表")
    @GetMapping("/categories/download")
    public ResponseEntity<byte[]> downloadCategoryList() {
        try {
            List<Category> categories = categoryRepository.findAll();
            String[] headers = {"分类ID", "分类名称", "完整路径"};
            List<String[]> data = new ArrayList<>();
            for (Category category : categories) {
                String fullPath = buildCategoryPath(category);
                data.add(new String[]{category.getId(), category.getName(), fullPath});
            }
            
            byte[] templateBytes = ExcelUtil.createDataExcel(headers, data);
            String fileName = "分类列表_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"))
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(templateBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String buildCategoryPath(Category category) {
        StringBuilder path = new StringBuilder(category.getName());
        Category parent = category.getParent();
        while (parent != null) {
            path.insert(0, parent.getName() + " > ");
            parent = parent.getParent();
        }
        return path.toString();
    }
}