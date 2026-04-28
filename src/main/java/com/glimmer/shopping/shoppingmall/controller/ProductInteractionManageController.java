package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/interaction")
@Api(tags = "商品管理-互动操作")
public class ProductInteractionManageController {

    @Autowired
    private ProductService productService;

    @ApiOperation("更新商品评分")
    @PutMapping("/rating")
    public Result<String> updateProductRating(@RequestBody ProductQueryRequest request) {
        return productService.updateProductRating(request.getId(), request.getRating());
    }

    @ApiOperation("增加商品浏览次数")
    @PostMapping("/view")
    public Result<String> incrementViewCount(@RequestBody ProductQueryRequest request) {
        return productService.incrementViewCount(request.getId());
    }

    @ApiOperation("增加商品收藏次数")
    @PostMapping("/favorite")
    public Result<String> incrementFavoriteCount(@RequestBody ProductQueryRequest request) {
        return productService.incrementFavoriteCount(request.getId());
    }
}