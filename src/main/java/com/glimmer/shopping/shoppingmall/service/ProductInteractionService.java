package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.ProductInteraction;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;
import java.util.Map;

/**
 * 商品互动服务接口（MongoDB）
 */
public interface ProductInteractionService {
    
    /**
     * 记录用户互动
     */
    Result<String> recordInteraction(String productId, String userId, String interactionType, 
                                    String clientIp, String userAgent, String sessionId);
    
    /**
     * 获取商品互动次数
     */
    Result<Long> getInteractionCount(String productId, String interactionType);
    
    /**
     * 获取商品互动统计
     */
    Result<Map<String, Long>> getProductInteractionStats(String productId);
    
    /**
     * 获取用户互动记录
     */
    Result<List<ProductInteraction>> getUserInteractions(String userId, String interactionType);
    
    /**
     * 检查用户是否已互动
     */
    Result<Boolean> hasUserInteracted(String productId, String userId, String interactionType);
    
    /**
     * 移除用户互动
     */
    Result<String> removeInteraction(String productId, String userId, String interactionType);
    
    /**
     * 获取最近互动记录（分页）
     */
    PageResult<ProductInteraction> getRecentInteractions(String productId, Integer limit);
}