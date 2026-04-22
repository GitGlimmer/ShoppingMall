package com.glimmer.shopping.shoppingmall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:user:";

    // 添加商品到购物车（hash: productId -> quantity）
    public void addToCart(Long userId, Long productId, Integer quantity) {
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.opsForHash().put(key, String.valueOf(productId), String.valueOf(quantity));
        redisTemplate.expire(key, Duration.ofDays(1));
    }

    public Map<Long, Integer> getCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<Long, Integer> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(Long.valueOf((String) k), Integer.valueOf((String) v)));
        return result;
    }
}
