package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.ProductInteraction;
import com.glimmer.shopping.shoppingmall.service.ProductInteractionService;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interaction")
@Api(tags = "商品互动管理")
public class ProductInteractionController {

    @Autowired
    private ProductInteractionService productInteractionService;

    @ApiOperation("记录用户互动")
    @PostMapping
    public Result<String> recordInteraction(@RequestBody ProductQueryRequest request) {
        return productInteractionService.recordInteraction(
                request.getId(),
                request.getUserId(),
                request.getInteractionType(),
                request.getClientIp(),
                request.getUserAgent(),
                request.getSessionId()
        );
    }

    @ApiOperation("获取商品互动统计")
    @PostMapping("/product/stats")
    public Result<Map<String, Long>> getProductInteractionStats(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getProductInteractionStats(request.getId());
    }

    @ApiOperation("获取商品指定类型互动次数")
    @PostMapping("/count")
    public Result<Long> getInteractionCount(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getInteractionCount(request.getId(), request.getInteractionType());
    }

    @ApiOperation("获取用户互动记录")
    @PostMapping("/user")
    public Result<List<ProductInteraction>> getUserInteractions(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getUserInteractions(request.getUserId(), request.getInteractionType());
    }

    @ApiOperation("检查用户是否已互动")
    @PostMapping("/check")
    public Result<Boolean> hasUserInteracted(@RequestBody ProductQueryRequest request) {
        return productInteractionService.hasUserInteracted(request.getId(), request.getUserId(), request.getInteractionType());
    }

    @ApiOperation("移除用户互动")
    @DeleteMapping
    public Result<String> removeInteraction(@RequestBody ProductQueryRequest request) {
        return productInteractionService.removeInteraction(request.getId(), request.getUserId(), request.getInteractionType());
    }

    @ApiOperation("获取最近互动记录")
    @PostMapping("/product/recent")
    public PageResult<ProductInteraction> getRecentInteractions(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getRecentInteractions(request.getId(), request.getLimit());
    }
}