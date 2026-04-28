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
@RequestMapping("/api/product/manage")
@Api(tags = "商品管理-基础操作")
public class ProductManageController {

    @Autowired
    private ProductService productService;

    @ApiOperation("新增商品")
    @PostMapping
    public Result<String> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @ApiOperation("批量新增商品")
    @PostMapping("/batch")
    public Result<List<String>> addProducts(@RequestBody List<Product> products) {
        return productService.addProducts(products);
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
    public Result<Product> getProduct(@RequestBody ProductQueryRequest request) {
        return productService.getProduct(request);
    }

    @ApiOperation("获取商品列表")
    @PostMapping("/list")
    public Result<List<Product>> getProductList() {
        return productService.getProductList();
    }

    @ApiOperation("获取商品详情（包含浏览计数）")
    @PostMapping("/detail/views")
    public Result<Product> getProductDetail(@RequestBody ProductQueryRequest request) {
        return productService.getProductDetail(request);
    }
}