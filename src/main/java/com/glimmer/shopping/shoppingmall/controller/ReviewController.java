package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.ProductReview;
import com.glimmer.shopping.shoppingmall.service.ReviewService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@Api(tags = "评论管理")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @ApiOperation("添加商品评论")
    @PostMapping
    public Result<ProductReview> addReview(@RequestBody ProductReview review) {
        return reviewService.addReview(review);
    }

    @ApiOperation("根据商品ID获取评论列表")
    @PostMapping("/product")
    public Result<List<ProductReview>> getReviewsByProduct(@RequestBody ProductQueryRequest request) {
        return reviewService.getReviewsByProduct(request.getId());
    }
}