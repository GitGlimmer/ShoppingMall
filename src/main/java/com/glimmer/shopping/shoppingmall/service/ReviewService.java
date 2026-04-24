package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.ProductReview;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;

/**
 * @author Glimmer
 */
public interface ReviewService {
    
    /**
     * 添加商品评论
     *
     * @param review
     * @return
     */
    Result<ProductReview> addReview(ProductReview review);

    /**
     * 根据商品ID获取评论列表
     *
     * @param productId
     * @return
     */
    Result<List<ProductReview>> getReviewsByProduct(String productId);
}