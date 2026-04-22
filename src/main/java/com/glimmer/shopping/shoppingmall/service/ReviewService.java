package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.ProductReview;
import com.glimmer.shopping.shoppingmall.repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ProductReviewRepository reviewRepository;

    public ProductReview addReview(ProductReview review) {
        review.setCreateTime(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public List<ProductReview> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
