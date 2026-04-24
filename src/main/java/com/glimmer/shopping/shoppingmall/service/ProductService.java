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
import java.util.List;

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

    /**
     * 修改商品
     * @param product
     * @return
     */
    public Result<Product> updateProduct(Product product) {
        // 检查商品是否存在
        if (!productRepository.existsById(product.getId())) {
            return Result.error("商品不存在");
        }
        // 更新数据库
        Product updatedProduct = productRepository.save(product);
        // 删除缓存
        redisTemplate.delete("product:" + product.getId());
        return Result.success(updatedProduct);
    }
    /**
     * 删除商品
     * @param id
     * @return
     */
    public Result deleteProduct(Long id) {
        // 检查商品是否存在
        if (!productRepository.existsById(id)) {
            return Result.error("商品不存在");
        }
        // 删除数据库中的商品
        productRepository.deleteById(id);
        // 删除缓存
        redisTemplate.delete("product:" + id);
        return Result.success();
    }

    /**
     * 获取商品列表
     * @return
     */
    public Result<List<Product>> getProductList() {
        List<Product> products = productRepository.findAll();
        return Result.success(products);
    }

}