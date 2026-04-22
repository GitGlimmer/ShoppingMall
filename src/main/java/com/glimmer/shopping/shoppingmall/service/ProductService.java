package com.glimmer.shopping.shoppingmall.service;

import com.alibaba.fastjson2.JSON;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * @author Glimmer
 */
@Service
@Slf4j
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 查询商品（带缓存）
     * @param id
     * @return
     */
    public Product getProduct(Long id) {
        String cacheKey = "product:" + id;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(cached, Product.class);
        }
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(product), Duration.ofMinutes(30));
        }
        return product;
    }

    /**
     * 更新库存
     * @param id
     * @param quantity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean reduceStock(Long id, int quantity) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null || product.getStock() < quantity) {
            return false;
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        // 删除缓存
        redisTemplate.delete("product:" + id);
        return true;
    }

    public Result<Product> addProduct(Product product) {
        //插入数据库
        return Result.success(productRepository.save(product));
    }
}
