package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.ProductInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品互动数据Repository（MongoDB）
 */
@Repository
public interface ProductInteractionRepository extends MongoRepository<ProductInteraction, String> {
    
    /**
     * 根据商品ID和互动类型查询互动记录
     */
    List<ProductInteraction> findByProductIdAndInteractionType(String productId, String interactionType);
    
    /**
     * 根据商品ID查询所有互动记录
     */
    List<ProductInteraction> findByProductId(String productId);
    
    /**
     * 根据用户ID和互动类型查询互动记录
     */
    List<ProductInteraction> findByUserIdAndInteractionType(String userId, String interactionType);
    
    /**
     * 统计商品的总互动次数
     */
    long countByProductIdAndInteractionType(String productId, String interactionType);
    
    /**
     * 统计用户在指定时间范围内的互动次数
     */
    long countByUserIdAndInteractionTypeAndInteractionTimeBetween(
            String userId, String interactionType, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询热门商品（按互动次数排序）
     */
    @Query(value = "{}", 
           sort = "{ interactionCount: -1 }", 
           fields = "{ productId: 1, interactionType: 1, interactionCount: 1 }")
    List<ProductInteraction> findPopularProducts();
    
    /**
     * 根据商品ID和时间范围查询互动记录
     */
    List<ProductInteraction> findByProductIdAndInteractionTimeBetween(String productId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据时间范围查询互动记录
     */
    List<ProductInteraction> findByInteractionTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 删除指定时间之前的互动记录（用于数据清理）
     */
    void deleteByInteractionTimeBefore(LocalDateTime time);
}