package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/query")
@Api(tags = "商品管理-商品查询")
public class ProductQueryController {

    @Autowired
    private ProductService productService;

    @ApiOperation("按分类查询商品")
    @PostMapping("/category")
    public Result<List<Product>> getProductsByCategory(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByCategory(request);
    }

    @ApiOperation("按品牌查询商品")
    @PostMapping("/brand")
    public Result<List<Product>> getProductsByBrand(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByBrand(request.getBrand());
    }

    @ApiOperation("按标签查询商品")
    @PostMapping("/tag")
    public Result<List<Product>> getProductsByTag(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByTag(request.getTag());
    }

    @ApiOperation("按产地查询商品")
    @PostMapping("/origin")
    public Result<List<Product>> getProductsByOrigin(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByOrigin(request.getOrigin());
    }

    @ApiOperation("按价格范围查询商品")
    @PostMapping("/price")
    public Result<List<Product>> getProductsByPriceRange(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByPriceRange(request);
    }

    @ApiOperation("搜索商品")
    @PostMapping("/search")
    public Result<List<Product>> searchProducts(@RequestBody ProductQueryRequest request) {
        return productService.searchProducts(request);
    }
}