package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/status")
@Api(tags = "商品管理-状态管理")
public class ProductStatusController {

    @Autowired
    private ProductService productService;

    @ApiOperation("更新商品状态（上架/下架）")
    @PostMapping
    public Result<String> updateProductStatus(@RequestBody ProductQueryRequest request) {
        return productService.updateProductStatus(request);
    }

    @ApiOperation("设置商品推荐状态")
    @PutMapping("/recommended")
    public Result<String> setRecommendedStatus(@RequestBody ProductQueryRequest request) {
        return productService.setRecommendedStatus(request.getId(), request.getIsRecommended());
    }

    @ApiOperation("设置商品新品状态")
    @PutMapping("/new")
    public Result<String> setNewStatus(@RequestBody ProductQueryRequest request) {
        return productService.setNewStatus(request.getId(), request.getIsNew());
    }

    @ApiOperation("设置商品热销状态")
    @PutMapping("/hot")
    public Result<String> setHotStatus(@RequestBody ProductQueryRequest request) {
        return productService.setHotStatus(request.getId(), request.getIsHot());
    }

    @ApiOperation("更新商品排序权重")
    @PutMapping("/sort")
    public Result<String> updateSortWeight(@RequestBody ProductQueryRequest request) {
        return productService.updateSortWeight(request.getId(), request.getLimit());
    }
}