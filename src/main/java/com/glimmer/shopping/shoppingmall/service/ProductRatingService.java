package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.ProductRating;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;
import java.util.Map;

/**
 * 商品评分服务接口
 */
public interface ProductRatingService {
    
    /**
     * 添加或更新评分
     */
    Result<String> addOrUpdateRating(String productId, String userId, Double rating, 
                                   String comment, Boolean isAnonymous, String clientIp, String userAgent);
    
    /**
     * 获取商品的平均评分
     */
    Result<Double> getAverageRating(String productId);
    
    /**
     * 获取商品的评分统计
     */
    Result<Map<String, Object>> getRatingStatistics(String productId);
    
    /**
     * 获取用户的评分记录
     */
    Result<List<ProductRating>> getUserRatings(String userId);
    
    /**
     * 获取商品的评分记录
     */
    Result<List<ProductRating>> getProductRatings(String productId, int page, int size);
    
    /**
     * 检查用户是否已评分
     */
    Result<Boolean> hasUserRated(String productId, String userId);
    
    /**
     * 删除评分记录
     */
    Result<String> deleteRating(String productId, String userId);
    
    /**
     * 获取热门评分商品（按评分数量排序）
     */
    Result<List<Map<String, Object>>> getTopRatedProducts(int limit);
    
    /**
     * 获取最新评分记录
     */
    Result<List<ProductRating>> getRecentRatings(int limit);
}