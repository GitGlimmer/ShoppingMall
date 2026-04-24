package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.Map;

/**
 * @author Glimmer
 */
public interface CartService {
    
    /**
     * 添加商品到购物车
     *
     * @param userId
     * @param productId
     * @param quantity
     */
    Result addToCart(String userId, String productId, Integer quantity);

    /**
     * 获取购物车内容
     *
     * @param userId
     * @return
     */
    Result<Map<String, Integer>> getCart(String userId);
}