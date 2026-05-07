package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductDTO;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.ExcelUtil;
import com.glimmer.shopping.shoppingmall.util.FileUtil;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @ApiOperation("批量导入商品")
    @PostMapping("/batch/upload")
    public Result<Integer> batchUploadProducts(@RequestParam("file") MultipartFile file) {
        if (!FileUtil.isValidExcelFile(file)) {
            return Result.error("请选择有效的Excel文件（.xlsx或.xls）");
        }
        try {
            return Result.success(0);
        } catch (Exception e) {
            return Result.error("批量导入失败");
        }
    }

    @ApiOperation("下载导入模板")
    @GetMapping("/template/download")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            String[] headers = {
                "商品名称", "分类", "品牌", "价格", "库存",
                "描述", "规格", "产地", "单位", "标签", "状态"
            };

            String[] exampleData = {
                "示例商品", "电子产品", "示例品牌", "99.99", "100",
                "这是一个示例商品描述", "规格说明", "中国", "件", "热门,新品", "1"
            };

            byte[] templateBytes = ExcelUtil.createTemplate(headers, exampleData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product_import_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(templateBytes);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}