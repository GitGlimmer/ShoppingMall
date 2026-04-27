package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.dto.CartRequest;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.Map;

/**
 * @author Glimmer
 */
public interface CartService {
    
    /**
     * 添加商品到购物车
     *
     * @param cartRequest 购物车请求参数
     */
    Result addToCart(CartRequest cartRequest);

    /**
     * 获取购物车内容
     *
     * @param userId
     * @return
     */
    Result<Map<String, Integer>> getCart(String userId);
}