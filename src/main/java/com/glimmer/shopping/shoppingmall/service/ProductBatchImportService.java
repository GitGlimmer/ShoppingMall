package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.util.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductBatchImportService {
    
    Result<Map<String, Object>> batchImport(MultipartFile file, String importMode, String brandName, String categoryName);
    
    Result<List<Map<String, String>>> getAllBrands();
    
    Result<List<Map<String, String>>> getAllCategories();
}