package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.dto.ProductDTO;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;

public interface ProductService {
    
    Result<ProductDTO> getProductDetail(ProductQueryRequest request);

    Result<String> addProduct(Product product);

    Result<String> updateProduct(Product product);

    Result<String> deleteProduct(ProductQueryRequest request);

    Result<String> updateProductStatus(ProductQueryRequest request);

    Result<PageResult<ProductDTO>> getProductsByPage(ProductQueryRequest request);

    Result<Integer> batchUpdateProductStatus(List<String> ids, Object status);
}
