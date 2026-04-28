package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.ProductRating;
import com.glimmer.shopping.shoppingmall.service.ProductRatingService;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rating")
@Api(tags = "商品评分管理")
public class ProductRatingController {

    @Autowired
    private ProductRatingService productRatingService;

    @ApiOperation("添加或更新商品评分")
    @PostMapping
    public Result<String> addOrUpdateRating(@RequestBody ProductQueryRequest request) {
        return productRatingService.addOrUpdateRating(
                request.getId(),
                request.getUserId(),
                request.getRating(),
                request.getComment(),
                request.getIsAnonymous(),
                request.getClientIp(),
                request.getUserAgent()
        );
    }

    @ApiOperation("获取商品平均评分")
    @PostMapping("/product/average")
    public Result<Double> getAverageRating(@RequestBody ProductQueryRequest request) {
        return productRatingService.getAverageRating(request.getId());
    }

    @ApiOperation("获取商品评分统计")
    @PostMapping("/product/stats")
    public Result<Map<String, Object>> getRatingStatistics(@RequestBody ProductQueryRequest request) {
        return productRatingService.getRatingStatistics(request.getId());
    }

    @ApiOperation("获取用户评分记录")
    @PostMapping("/user")
    public Result<List<ProductRating>> getUserRatings(@RequestBody ProductQueryRequest request) {
        return productRatingService.getUserRatings(request.getUserId());
    }

    @ApiOperation("获取商品评分列表")
    @PostMapping("/product/list")
    public PageResult<ProductRating> getProductRatings(@RequestBody ProductQueryRequest request) {
        return productRatingService.getProductRatings(request.getId(), request.getPage(), request.getSize());
    }

    @ApiOperation("检查用户是否已评分")
    @PostMapping("/check")
    public Result<Boolean> hasUserRated(@RequestBody ProductQueryRequest request) {
        return productRatingService.hasUserRated(request.getId(), request.getUserId());
    }

    @ApiOperation("删除评分记录")
    @DeleteMapping
    public Result<String> deleteRating(@RequestBody ProductQueryRequest request) {
        return productRatingService.deleteRating(request.getId(), request.getUserId());
    }

    @ApiOperation("获取热门评分商品")
    @PostMapping("/top")
    public Result<List<Map<String, Object>>> getTopRatedProductsList(@RequestBody ProductQueryRequest request) {
        return productRatingService.getTopRatedProducts(request.getLimit());
    }

    @ApiOperation("获取最新评分记录")
    @PostMapping("/recent")
    public Result<List<ProductRating>> getRecentRatings(@RequestBody ProductQueryRequest request) {
        return productRatingService.getRecentRatings(request.getLimit());
    }
}