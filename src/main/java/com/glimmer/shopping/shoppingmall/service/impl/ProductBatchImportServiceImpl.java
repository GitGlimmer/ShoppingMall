package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.entity.Brand;
import com.glimmer.shopping.shoppingmall.entity.Category;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.repository.BrandRepository;
import com.glimmer.shopping.shoppingmall.repository.CategoryRepository;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import com.glimmer.shopping.shoppingmall.service.ProductBatchImportService;
import com.glimmer.shopping.shoppingmall.util.Result;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductBatchImportServiceImpl implements ProductBatchImportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Result<Map<String, Object>> batchImport(MultipartFile file, String importMode, String brandName, String categoryName) {
        Map<String, Object> result = new HashMap<>();
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            
            Map<String, String> brandNameToIdMap = buildBrandNameToIdMap();
            Map<String, String> categoryNameToIdMap = buildCategoryNameToIdMap();
            
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Product product = parseRowToProduct(row, brandNameToIdMap, categoryNameToIdMap, true);
                    
                    if (product != null) {
                        product.setId(UUID.randomUUID().toString());
                        product.setCreateTime(LocalDateTime.now());
                        product.setUpdateTime(LocalDateTime.now());
                        productRepository.save(product);
                        successList.add(product.getName());
                    }
                } catch (Exception e) {
                    String productName = getCellValueAsString(row.getCell(0));
                    failList.add(productName + " - " + e.getMessage());
                }
            }
            
            result.put("successCount", successList.size());
            result.put("failCount", failList.size());
            result.put("successItems", successList);
            result.put("failItems", failList);
            
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, String>>> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        List<Map<String, String>> result = new ArrayList<>();
        
        for (Brand brand : brands) {
            Map<String, String> brandMap = new HashMap<>();
            brandMap.put("id", brand.getId());
            brandMap.put("name", brand.getName());
            result.add(brandMap);
        }
        
        return Result.success(result);
    }

    @Override
    public Result<List<Map<String, String>>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<Map<String, String>> result = new ArrayList<>();
        
        for (Category category : categories) {
            Map<String, String> categoryMap = new HashMap<>();
            categoryMap.put("id", category.getId());
            categoryMap.put("name", category.getName());
            result.add(categoryMap);
        }
        
        return Result.success(result);
    }

    private Map<String, String> buildBrandNameToIdMap() {
        Map<String, String> map = new HashMap<>();
        List<Brand> brands = brandRepository.findAll();
        
        for (Brand brand : brands) {
            map.put(brand.getName(), brand.getId());
        }
        
        return map;
    }

    private Map<String, String> buildCategoryNameToIdMap() {
        Map<String, String> map = new HashMap<>();
        List<Category> categories = categoryRepository.findAll();
        
        for (Category category : categories) {
            map.put(category.getName(), category.getId());
        }
        
        return map;
    }

    private Product parseRowToProduct(Row row, Map<String, String> brandNameToIdMap, 
                                      Map<String, String> categoryNameToIdMap,
                                      boolean isStrictMode) {
        
        String productName = getCellValueAsString(row.getCell(0));
        String categoryInput = getCellValueAsString(row.getCell(1));
        String brandInput = getCellValueAsString(row.getCell(2));
        Double price = getCellValueAsDouble(row.getCell(3));
        Integer stock = getCellValueAsInteger(row.getCell(4));
        String description = getCellValueAsString(row.getCell(5));
        String specification = getCellValueAsString(row.getCell(6));
        String origin = getCellValueAsString(row.getCell(7));
        String unit = getCellValueAsString(row.getCell(8));
        String tags = getCellValueAsString(row.getCell(9));
        Integer status = getCellValueAsInteger(row.getCell(10));
        
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        
        Product product = new Product();
        product.setName(productName);
        
        String categoryId = resolveCategoryId(categoryInput, categoryNameToIdMap);
        if (categoryId == null) {
            throw new IllegalArgumentException("分类不能为空或不存在: " + categoryInput);
        }
        product.setCategoryId(categoryId);
        
        String brandId = resolveBrandId(brandInput, brandNameToIdMap);
        if (brandId == null) {
            throw new IllegalArgumentException("品牌不能为空或不存在: " + brandInput);
        }
        product.setBrandId(brandId);
        
        product.setPrice(price != null ? price : 0.0);
        product.setStock(stock != null ? stock : 0);
        product.setDescription(description);
        product.setSpecifications(specification);
        product.setOrigin(origin);
        product.setUnit(unit != null ? unit : "件");
        product.setTags(tags);
        product.setStatus(status != null ? status : 1);
        
        return product;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        Double value = getCellValueAsDouble(cell);
        return value != null ? value.intValue() : null;
    }

    private String resolveCategoryId(String input, Map<String, String> categoryNameToIdMap) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        input = input.trim();
        
        if (categoryNameToIdMap.containsKey(input)) {
            return categoryNameToIdMap.get(input);
        }
        
        Optional<Category> categoryOpt = categoryRepository.findById(input);
        if (categoryOpt.isPresent()) {
            return input;
        }
        
        return null;
    }

    private String resolveBrandId(String input, Map<String, String> brandNameToIdMap) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        input = input.trim();
        
        if (brandNameToIdMap.containsKey(input)) {
            return brandNameToIdMap.get(input);
        }
        
        Optional<Brand> brandOpt = brandRepository.findById(input);
        if (brandOpt.isPresent()) {
            return input;
        }
        
        return null;
    }
}