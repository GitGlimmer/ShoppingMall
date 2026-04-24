package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.service.CartService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Glimmer
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:user:";

    @Override
    public Result addToCart(String userId, String productId, Integer quantity) {
        try {
            String key = CART_KEY_PREFIX + userId;
            redisTemplate.opsForHash().put(key, String.valueOf(productId), String.valueOf(quantity));
            redisTemplate.expire(key, Duration.ofDays(1));
            return Result.success();
        } catch (Exception e) {
            log.error("添加购物车失败，用户ID: {}, 商品ID: {}, 数量: {}", userId, productId, quantity, e);
            return Result.error("添加购物车失败");
        }
    }

    @Override
    public Result<Map<String, Integer>> getCart(String userId) {
        try {
            String key = CART_KEY_PREFIX + userId;
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            Map<String, Integer> result = new HashMap<>();
            entries.forEach((k, v) -> result.put((String) k, Integer.valueOf((String) v)));
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取购物车失败，用户ID: {}", userId, e);
            return Result.error("获取购物车失败");
        }
    }
}