package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.dto.CartRequest;
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
    public Result addToCart(CartRequest cartRequest) {
        try {
            // 参数验证
            if (cartRequest == null) {
                return Result.error("请求参数不能为空");
            }
            
            String userId = cartRequest.getUserId();
            String productId = cartRequest.getProductId();
            Integer quantity = cartRequest.getQuantity();
            
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("用户ID不能为空");
            }
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (quantity == null || quantity <= 0) {
                return Result.error("商品数量必须大于0");
            }
            
            String key = CART_KEY_PREFIX + userId;
            redisTemplate.opsForHash().put(key, productId, quantity.toString());
            redisTemplate.expire(key, Duration.ofDays(30));
            return Result.success();
        } catch (Exception e) {
            log.error("添加购物车失败，请求参数: {}", cartRequest, e);
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