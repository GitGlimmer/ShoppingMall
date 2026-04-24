package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.entity.ProductReview;
import com.glimmer.shopping.shoppingmall.repository.ProductReviewRepository;
import com.glimmer.shopping.shoppingmall.service.ReviewService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Glimmer
 */
@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ProductReviewRepository reviewRepository;

    @Override
    public Result<ProductReview> addReview(ProductReview review) {
        try {
            // 保存评论，实体类会自动生成UUID和创建时间
            ProductReview savedReview = reviewRepository.save(review);
            return Result.success(savedReview);
        } catch (Exception e) {
            log.error("添加评论失败，评论信息: {}", review, e);
            return Result.error("添加评论失败");
        }
    }

    @Override
    public Result<List<ProductReview>> getReviewsByProduct(String productId) {
        try {
            List<ProductReview> reviews = reviewRepository.findByProductId(productId);
            return Result.success(reviews);
        } catch (Exception e) {
            log.error("获取商品评论失败，商品ID: {}", productId, e);
            return Result.error("获取商品评论失败");
        }
    }
}