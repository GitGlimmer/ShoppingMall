package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.dto.SalesQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.ProductStats;
import com.glimmer.shopping.shoppingmall.service.AnalyticsService;
import com.glimmer.shopping.shoppingmall.service.ClickHouseService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Api(tags = "数据统计")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private ClickHouseService clickHouseService;

    @ApiOperation("获取销售数据")
    @PostMapping("/sales")
    public Result<List<Map<String, Object>>> getSales(@RequestBody SalesQueryRequest request) {
        return analyticsService.getSales(request);
    }

    @ApiOperation("获取商品历史统计数据")
    @PostMapping("/stats/product/history")
    public Result<List<ProductStats>> getProductStatsHistory(@RequestBody ProductQueryRequest request) {
        return Result.success(clickHouseService.getProductStatsHistory(
                request.getId(),
                request.getMinRating() != null ? LocalDate.now().minusDays(request.getMinRating().longValue()) : LocalDate.now().minusDays(30),
                LocalDate.now()
        ));
    }

    @ApiOperation("获取分类销售统计")
    @PostMapping("/stats/category/sales")
    public Result<List<ProductStats>> getCategorySalesStats() {
        return Result.success(clickHouseService.getCategorySalesStats(LocalDate.now()));
    }
}