package com.glimmer.shopping.shoppingmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author Glimmer
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 查询商品（带缓存）
     *
     * @param id
     * @return
     */
    @Override
    public Result<Product> getProduct(String id) {
        try {
            String cacheKey = "product:" + id;
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(JSON.parseObject(cached, Product.class));
            }
            Product product = productRepository.findById(id).orElse(null);
            if (product != null) {
                redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(product), Duration.ofMinutes(30));
                return Result.success(product);
            } else {
                return Result.error("商品不存在");
            }
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID: {}", id, e);
            return Result.error("获取商品详情失败");
        }
    }

    /**
     * 更新库存
     *
     * @param id
     * @param quantity
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> reduceStock(String id, int quantity) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            // 将字符串库存转换为整数进行比较
            int currentStock = Integer.parseInt(product.getStock());
            if (currentStock < quantity) {
                return Result.error("库存不足");
            }
            
            // 计算新库存并转换为字符串
            int newStock = currentStock - quantity;
            product.setStock(String.valueOf(newStock));
            
            productRepository.save(product);
            // 删除缓存
            redisTemplate.delete("product:" + id);
            return Result.success(true);
        } catch (NumberFormatException e) {
            log.error("库存格式错误，商品ID: {}", id, e);
            return Result.error("库存数据格式错误");
        } catch (Exception e) {
            log.error("更新库存失败，商品ID: {}, 数量: {}", id, quantity, e);
            return Result.error("更新库存失败");
        }
    }

    @Override
    public Result<String> addProduct(Product product) {
        try {
            // 插入数据库，实体类会自动生成UUID
            Product savedProduct = productRepository.save(product);
            return Result.success(savedProduct.getId());
        } catch (Exception e) {
            log.error("新增商品失败，商品信息: {}", product, e);
            return Result.error("新增商品失败");
        }
    }

    /**
     * 修改商品
     *
     * @param product
     * @return
     */
    @Override
    public Result<String> updateProduct(Product product) {
        try {
            // 检查商品是否存在
            if (!productRepository.existsById(product.getId())) {
                return Result.error("商品不存在");
            }
            // 更新数据库
            Product updatedProduct = productRepository.save(product);
            // 删除缓存
            redisTemplate.delete("product:" + product.getId());
            return Result.success(updatedProduct.getId());
        } catch (Exception e) {
            log.error("修改商品失败，商品信息: {}", product, e);
            return Result.error("修改商品失败");
        }
    }

    /**
     * 删除商品
     *
     * @param id
     * @return
     */
    @Override
    public Result<String> deleteProduct(String id) {
        try {
            // 检查商品是否存在
            if (!productRepository.existsById(id)) {
                return Result.error("商品不存在");
            }
            // 删除数据库中的商品
            productRepository.deleteById(id);
            // 删除缓存
            redisTemplate.delete("product:" + id);
            return Result.success(id);
        } catch (Exception e) {
            log.error("删除商品失败，商品ID: {}", id, e);
            return Result.error("删除商品失败");
        }
    }

    /**
     * 批量新增商品
     *
     * @param products
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<String>> addProducts(List<Product> products) {
        try {
            if (products == null || products.isEmpty()) {
                return Result.error("商品列表不能为空");
            }
            
            // 批量保存商品
            List<Product> savedProducts = productRepository.saveAll(products);
            
            // 提取商品id列表
            List<String> productIds = savedProducts.stream()
                    .map(Product::getId)
                    .collect(java.util.stream.Collectors.toList());
            
            // 清除相关缓存
            for (Product product : savedProducts) {
                redisTemplate.delete("product:" + product.getId());
            }
            
            return Result.success(productIds);
        } catch (Exception e) {
            log.error("批量新增商品失败，商品数量: {}", products != null ? products.size() : 0, e);
            return Result.error("批量新增商品失败");
        }
    }

    /**
     * 获取商品列表
     *
     * @return
     */
    @Override
    public Result<List<Product>> getProductList() {
        try {
            List<Product> products = productRepository.findAll();
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return Result.error("获取商品列表失败");
        }
    }

    /**
     * 下单
     *
     * @param request
     * @return
     */
    @Override
    public Result<String> createOrder(OrderRequest request) {
        try {
            Result<Boolean> stockResult = reduceStock(request.getProductId(), request.getQuantity());
            if (stockResult.getCode() == 200 && Boolean.TRUE.equals(stockResult.getData())) {
                return Result.success();
            } else {
                return Result.error("下单失败，库存不足或商品不存在");
            }
        } catch (Exception e) {
            log.error("下单失败，订单信息: {}", request, e);
            return Result.error("下单失败");
        }
    }
}