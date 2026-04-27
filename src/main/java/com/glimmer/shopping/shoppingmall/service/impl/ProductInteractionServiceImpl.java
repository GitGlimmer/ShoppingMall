package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.entity.ProductInteraction;
import com.glimmer.shopping.shoppingmall.repository.ProductInteractionRepository;
import com.glimmer.shopping.shoppingmall.service.ProductInteractionService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品互动服务实现（MongoDB）
 */
@Service
@Slf4j
public class ProductInteractionServiceImpl implements ProductInteractionService {
    
    @Autowired
    private ProductInteractionRepository productInteractionRepository;
    
    @Override
    public Result<String> recordInteraction(String productId, String userId, String interactionType, 
                                          String clientIp, String userAgent, String sessionId) {
        try {
            ProductInteraction interaction = new ProductInteraction();
            interaction.setProductId(productId);
            interaction.setUserId(userId);
            interaction.setInteractionType(interactionType);
            interaction.setClientIp(clientIp);
            interaction.setUserAgent(userAgent);
            interaction.setSessionId(sessionId);
            interaction.prePersist();
            
            ProductInteraction saved = productInteractionRepository.save(interaction);
            return Result.success(saved.getId());
        } catch (Exception e) {
            log.error("记录互动数据失败，商品ID: {}, 用户ID: {}, 互动类型: {}", productId, userId, interactionType, e);
            return Result.error("记录互动数据失败");
        }
    }
    
    @Override
    public Result<Long> getInteractionCount(String productId, String interactionType) {
        try {
            long count = productInteractionRepository.countByProductIdAndInteractionType(productId, interactionType);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取互动次数失败，商品ID: {}, 互动类型: {}", productId, interactionType, e);
            return Result.error("获取互动次数失败");
        }
    }
    
    @Override
    public Result<Map<String, Long>> getProductInteractionStats(String productId) {
        try {
            Map<String, Long> stats = new HashMap<>();
            
            // 统计浏览数
            long viewCount = productInteractionRepository.countByProductIdAndInteractionType(productId, "VIEW");
            stats.put("viewCount", viewCount);
            
            // 统计收藏数
            long favoriteCount = productInteractionRepository.countByProductIdAndInteractionType(productId, "FAVORITE");
            stats.put("favoriteCount", favoriteCount);
            
            // 统计点赞数
            long likeCount = productInteractionRepository.countByProductIdAndInteractionType(productId, "LIKE");
            stats.put("likeCount", likeCount);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取商品互动统计失败，商品ID: {}", productId, e);
            return Result.error("获取商品互动统计失败");
        }
    }
    
    @Override
    public Result<List<ProductInteraction>> getUserInteractions(String userId, String interactionType) {
        try {
            List<ProductInteraction> interactions = productInteractionRepository.findByUserIdAndInteractionType(userId, interactionType);
            return Result.success(interactions);
        } catch (Exception e) {
            log.error("获取用户互动记录失败，用户ID: {}, 互动类型: {}", userId, interactionType, e);
            return Result.error("获取用户互动记录失败");
        }
    }
    
    @Override
    public Result<Boolean> hasUserInteracted(String productId, String userId, String interactionType) {
        try {
            List<ProductInteraction> interactions = productInteractionRepository.findByProductIdAndInteractionType(productId, interactionType);
            boolean hasInteracted = interactions.stream()
                    .anyMatch(interaction -> userId.equals(interaction.getUserId()));
            return Result.success(hasInteracted);
        } catch (Exception e) {
            log.error("检查用户互动状态失败，商品ID: {}, 用户ID: {}, 互动类型: {}", productId, userId, interactionType, e);
            return Result.error("检查用户互动状态失败");
        }
    }
    
    @Override
    public Result<String> removeInteraction(String productId, String userId, String interactionType) {
        try {
            List<ProductInteraction> interactions = productInteractionRepository.findByProductIdAndInteractionType(productId, interactionType);
            interactions.stream()
                    .filter(interaction -> userId.equals(interaction.getUserId()))
                    .findFirst()
                    .ifPresent(interaction -> productInteractionRepository.delete(interaction));
            
            return Result.success("移除互动成功");
        } catch (Exception e) {
            log.error("移除互动记录失败，商品ID: {}, 用户ID: {}, 互动类型: {}", productId, userId, interactionType, e);
            return Result.error("移除互动记录失败");
        }
    }
    
    @Override
    public Result<List<ProductInteraction>> getRecentInteractions(String productId, int limit) {
        try {
            List<ProductInteraction> allInteractions = productInteractionRepository.findByProductId(productId);
            List<ProductInteraction> recentInteractions = allInteractions.stream()
                    .sorted((a, b) -> b.getInteractionTime().compareTo(a.getInteractionTime()))
                    .limit(limit)
                    .collect(Collectors.toList());
            return Result.success(recentInteractions);
        } catch (Exception e) {
            log.error("获取最近互动记录失败，商品ID: {}", productId, e);
            return Result.error("获取最近互动记录失败");
        }
    }
}