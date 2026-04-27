package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.ProductRating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 商品评分数据Repository（MongoDB）
 */
@Repository
public interface ProductRatingRepository extends MongoRepository<ProductRating, String> {
    
    /**
     * 根据商品ID查询评分记录
     */
    List<ProductRating> findByProductId(String productId);
    
    /**
     * 根据用户ID查询评分记录
     */
    List<ProductRating> findByUserId(String userId);
    
    /**
     * 根据商品ID和用户ID查询评分记录
     */
    Optional<ProductRating> findByProductIdAndUserId(String productId, String userId);
    
    /**
     * 统计商品的平均评分
     */
    @Query(value = "{'productId': ?0}", fields = "{'rating': 1}")
    List<ProductRating> findRatingsByProductId(String productId);
    
    /**
     * 统计商品的评分数量
     */
    long countByProductId(String productId);
    
    /**
     * 根据评分范围查询评分记录
     */
    List<ProductRating> findByProductIdAndRatingBetween(String productId, Double minRating, Double maxRating);
    
    /**
     * 根据时间范围查询评分记录
     */
    List<ProductRating> findByRatingTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询高评分记录（4分以上）
     */
    List<ProductRating> findByRatingGreaterThanEqual(Double minRating);
    
    /**
     * 查询低评分记录（3分以下）
     */
    List<ProductRating> findByRatingLessThan(Double maxRating);
    
    /**
     * 删除指定时间之前的评分记录（用于数据清理）
     */
    void deleteByRatingTimeBefore(LocalDateTime time);
}