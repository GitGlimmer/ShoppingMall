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
import java.util.Map;

@RestController
@RequestMapping("/api/product/stats")
@Api(tags = "商品管理-统计排行")
public class ProductStatsController {

    @Autowired
    private ProductService productService;

    @ApiOperation("获取商品统计信息")
    @PostMapping
    public Result<Map<String, Object>> getProductStatistics() {
        return productService.getProductStatistics();
    }

    @ApiOperation("获取热门商品")
    @PostMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestBody ProductQueryRequest request) {
        return productService.getHotProducts(request);
    }

    @ApiOperation("获取推荐商品列表")
    @PostMapping("/recommended")
    public Result<List<Product>> getRecommendedProducts() {
        return productService.getRecommendedProducts();
    }

    @ApiOperation("获取新品列表")
    @PostMapping("/new")
    public Result<List<Product>> getNewProducts() {
        return productService.getNewProducts();
    }

    @ApiOperation("获取商品评分排行榜")
    @PostMapping("/rating/top")
    public Result<List<Product>> getTopRatedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getTopRatedProducts(request.getLimit());
    }

    @ApiOperation("获取浏览最多的商品")
    @PostMapping("/view/top")
    public Result<List<Product>> getMostViewedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getMostViewedProducts(request.getLimit());
    }

    @ApiOperation("获取收藏最多的商品")
    @PostMapping("/favorite/top")
    public Result<List<Product>> getMostFavoritedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getMostFavoritedProducts(request.getLimit());
    }
}