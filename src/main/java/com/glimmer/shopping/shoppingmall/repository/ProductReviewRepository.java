package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.ProductReview;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Glimmer
 */
public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {
    /**
     * 根据商品ID查询评论
     * @param productId
     * @return
     */
    List<ProductReview> findByProductId(String productId);
}