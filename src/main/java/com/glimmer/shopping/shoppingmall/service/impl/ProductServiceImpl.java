package com.glimmer.shopping.shoppingmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.entity.ProductStats;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import com.glimmer.shopping.shoppingmall.service.ClickHouseService;
import com.glimmer.shopping.shoppingmall.service.ProductInteractionService;
import com.glimmer.shopping.shoppingmall.service.ProductRatingService;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private ProductInteractionService productInteractionService;

    @Autowired
    private ClickHouseService clickHouseService;

    @Autowired
    private ProductRatingService productRatingService;

    /**
     * 查询商品（带缓存）
     *
     * @param request 查询请求
     * @return
     */
    @Override
    public Result<Product> getProduct(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String id = request.getId();
            if (id == null || id.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            // 先从缓存中获取
            String cacheKey = "product:" + id;
            String cachedProduct = redisTemplate.opsForValue().get(cacheKey);
            if (cachedProduct != null) {
                Product product = JSON.parseObject(cachedProduct, Product.class);
                return Result.success(product);
            }
            
            // 缓存中没有，从数据库获取
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            // 存入缓存
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(product), Duration.ofHours(1));
            
            return Result.success(product);
        } catch (Exception e) {
            log.error("获取商品详情失败，请求参数: {}", request, e);
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
     * @param request
     * @return
     */
    @Override
    public Result<String> deleteProduct(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String id = request.getId();
            if (id == null || id.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
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
            log.error("删除商品失败，请求参数: {}", request, e);
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
                    .collect(Collectors.toList());
            
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
     * 按分类查询商品
     */
    @Override
    public Result<List<Product>> getProductsByCategory(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String category = request.getCategory();
            if (category == null || category.trim().isEmpty()) {
                return Result.error("商品分类不能为空");
            }
            
            List<Product> products = productRepository.findByCategory(category);
            return Result.success(products);
        } catch (Exception e) {
            log.error("按分类查询商品失败，请求参数: {}", request, e);
            return Result.error("按分类查询商品失败");
        }
    }

    /**
     * 按关键字搜索商品
     */
    @Override
    public Result<List<Product>> searchProducts(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String keyword = request.getKeyword();
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("搜索关键字不能为空");
            }
            
            List<Product> products = productRepository.findByNameContaining(keyword);
            return Result.success(products);
        } catch (Exception e) {
            log.error("搜索商品失败，请求参数: {}", request, e);
            return Result.error("搜索商品失败");
        }
    }

    /**
     * 按价格范围查询商品
     */
    @Override
    public Result<List<Product>> getProductsByPriceRange(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String minPrice = request.getMinPrice();
            String maxPrice = request.getMaxPrice();
            
            if (minPrice == null || maxPrice == null) {
                return Result.error("价格范围不能为空");
            }
            
            // 验证价格格式
            try {
                BigDecimal min = new BigDecimal(minPrice);
                BigDecimal max = new BigDecimal(maxPrice);
                
                if (min.compareTo(max) > 0) {
                    return Result.error("最低价格不能高于最高价格");
                }
            } catch (NumberFormatException e) {
                return Result.error("价格格式错误");
            }
            
            List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
            return Result.success(products);
        } catch (NumberFormatException e) {
            log.error("价格格式错误，请求参数: {}", request, e);
            return Result.error("价格格式错误");
        } catch (Exception e) {
            log.error("按价格范围查询商品失败，请求参数: {}", request, e);
            return Result.error("按价格范围查询商品失败");
        }
    }

    /**
     * 获取热门商品（按销量排序）
     */
    @Override
    public Result<List<Product>> getHotProducts(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            // 处理默认值（业务逻辑应该在Service层）
            Integer limit = request.getLimit() != null ? request.getLimit() : 10;
            
            if (limit <= 0) {
                return Result.error("查询数量必须大于0");
            }
            
            List<Product> products = productRepository.findHotProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取热门商品失败，请求参数: {}", request, e);
            return Result.error("获取热门商品失败");
        }
    }

    /**
     * 更新商品状态（上架/下架）
     */
    @Override
    public Result<String> updateProductStatus(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String productId = request.getId();
            String status = request.getStatus();
            
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (status == null || status.trim().isEmpty()) {
                return Result.error("商品状态不能为空");
            }
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            if (!"ON_SALE".equals(status) && !"OFF_SALE".equals(status)) {
                return Result.error("商品状态不合法");
            }
            
            product.setStatus(status);
            product.setUpdateTime(LocalDateTime.now());
            Product updatedProduct = productRepository.save(product);
            
            // 清除缓存
            redisTemplate.delete("product:" + productId);
            
            return Result.success(updatedProduct.getId());
        } catch (Exception e) {
            log.error("更新商品状态失败，请求参数: {}", request, e);
            return Result.error("更新商品状态失败");
        }
    }

    /**
     * 获取商品统计信息
     */
    @Override
    public Result<Map<String, Object>> getProductStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计商品总数
            Long totalProducts = productRepository.countAllProducts();
            statistics.put("totalProducts", totalProducts);
            
            // 统计上架商品数量
            Long onSaleProducts = productRepository.countOnSaleProducts();
            statistics.put("onSaleProducts", onSaleProducts);
            
            // 统计下架商品数量
            Long offSaleProducts = productRepository.countOffSaleProducts();
            statistics.put("offSaleProducts", offSaleProducts);
            
            // 计算上架率
            if (totalProducts > 0) {
                double onSaleRate = (double) onSaleProducts / totalProducts * 100;
                statistics.put("onSaleRate", String.format("%.2f%%", onSaleRate));
            } else {
                statistics.put("onSaleRate", "0.00%");
            }
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取商品统计信息失败", e);
            return Result.error("获取商品统计信息失败");
        }
    }

    /**
     * 下单
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createOrder(OrderRequest request) {
        try {
            Result<Boolean> stockResult = reduceStock(request.getProductId(), request.getQuantity());
            if (stockResult.getCode() == 200 && Boolean.TRUE.equals(stockResult.getData())) {
                // 增加商品销量
                Product product = productRepository.findById(request.getProductId()).orElse(null);
                if (product != null) {
                    product.setSales(product.getSales() + request.getQuantity());
                    product.setUpdateTime(LocalDateTime.now());
                    productRepository.save(product);
                    
                    // 清除缓存
                    redisTemplate.delete("product:" + request.getProductId());
                }
                
                return Result.success("下单成功");
            } else {
                return Result.error("下单失败，库存不足或商品不存在");
            }
        } catch (Exception e) {
            log.error("下单失败，订单信息: {}", request, e);
            return Result.error("下单失败");
        }
    }

    @Override
    public Result<List<Product>> getRecommendedProducts() {
        try {
            List<Product> products = productRepository.findByIsRecommendedTrue();
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取推荐商品失败", e);
            return Result.error("获取推荐商品失败");
        }
    }

    @Override
    public Result<List<Product>> getNewProducts() {
        try {
            List<Product> products = productRepository.findByIsNewTrue();
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取新品列表失败", e);
            return Result.error("获取新品列表失败");
        }
    }

    @Override
    public Result<List<Product>> getProductsByBrand(String brand) {
        try {
            if (brand == null || brand.trim().isEmpty()) {
                return Result.error("品牌名称不能为空");
            }
            List<Product> products = productRepository.findByBrand(brand);
            return Result.success(products);
        } catch (Exception e) {
            log.error("按品牌查询商品失败，品牌: {}", brand, e);
            return Result.error("按品牌查询商品失败");
        }
    }

    @Override
    public Result<List<Product>> getProductsByTag(String tag) {
        try {
            if (tag == null || tag.trim().isEmpty()) {
                return Result.error("标签不能为空");
            }
            List<Product> products = productRepository.findByTag(tag);
            return Result.success(products);
        } catch (Exception e) {
            log.error("按标签查询商品失败，标签: {}", tag, e);
            return Result.error("按标签查询商品失败");
        }
    }

    @Override
    public Result<List<Product>> getProductsByOrigin(String origin) {
        try {
            if (origin == null || origin.trim().isEmpty()) {
                return Result.error("产地不能为空");
            }
            List<Product> products = productRepository.findByOrigin(origin);
            return Result.success(products);
        } catch (Exception e) {
            log.error("按产地查询商品失败，产地: {}", origin, e);
            return Result.error("按产地查询商品失败");
        }
    }

    @Override
    public Result<List<Product>> getTopRatedProducts(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            // 使用新的评分服务获取热门评分商品
            Result<List<Map<String, Object>>> ratingResult = productRatingService.getTopRatedProducts(limit);
            
            if (ratingResult.getCode() != 200) {
                return Result.error(ratingResult.getMessage());
            }
            
            List<Map<String, Object>> ratedProducts = ratingResult.getData();
            
            // 从MySQL获取商品详细信息
            List<Product> products = new ArrayList<>();
            for (Map<String, Object> ratedProduct : ratedProducts) {
                String productId = (String) ratedProduct.get("productId");
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    products.add(product);
                }
            }
            
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取评分排行榜失败，限制数量: {}", limit, e);
            return Result.error("获取评分排行榜失败");
        }
    }

    @Override
    public Result<List<Product>> getMostViewedProducts(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            // 从ClickHouse获取热门浏览数据
            List<ProductStats> statsList = clickHouseService.getMostViewedProducts(limit);
            
            // 从MySQL获取商品详细信息
            List<Product> products = new ArrayList<>();
            for (ProductStats stats : statsList) {
                Product product = productRepository.findById(stats.getProductId()).orElse(null);
                if (product != null) {
                    products.add(product);
                }
            }
            
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取浏览最多的商品失败，限制数量: {}", limit, e);
            return Result.error("获取浏览最多的商品失败");
        }
    }

    @Override
    public Result<List<Product>> getMostFavoritedProducts(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            // 从ClickHouse获取热门收藏数据
            List<ProductStats> statsList = clickHouseService.getMostFavoritedProducts(limit);
            
            // 从MySQL获取商品详细信息
            List<Product> products = new ArrayList<>();
            for (ProductStats stats : statsList) {
                Product product = productRepository.findById(stats.getProductId()).orElse(null);
                if (product != null) {
                    products.add(product);
                }
            }
            
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取收藏最多的商品失败，限制数量: {}", limit, e);
            return Result.error("获取收藏最多的商品失败");
        }
    }

    @Override
    public Result<String> updateProductRating(String productId, Double rating) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (rating == null || rating < 0 || rating > 5) {
                return Result.error("评分必须在0-5之间");
            }
            
            // 在实际应用中，这里应该传入真实的用户ID和其他参数
            // 目前先使用默认值，后续可以根据业务需求调整
            String userId = "anonymous_user"; // 默认匿名用户
            String comment = "用户评分";
            Boolean isAnonymous = true;
            String clientIp = "127.0.0.1";
            String userAgent = "API调用";
            
            // 使用新的评分服务处理评分数据
            Result<String> result = productRatingService.addOrUpdateRating(
                    productId, userId, rating, comment, isAnonymous, clientIp, userAgent);
            
            if (result.getCode() == 200) {
                // 记录评分互动到MongoDB
                productInteractionService.recordInteraction(productId, userId, "RATING", clientIp, userAgent, null);
                
                // 清除商品缓存，确保下次获取时能反映最新的评分数据
                redisTemplate.delete("product:" + productId);
                
                log.info("商品评分记录成功，商品ID: {}, 用户ID: {}, 评分: {}", productId, userId, rating);
                return Result.success("评分记录成功");
            } else {
                return result;
            }
        } catch (Exception e) {
            log.error("更新商品评分失败，商品ID: {}, 评分: {}", productId, rating, e);
            return Result.error("更新商品评分失败");
        }
    }

    @Override
    public Result<String> incrementViewCount(String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            // 记录浏览互动（MongoDB）
            productInteractionService.recordInteraction(productId, null, "VIEW", null, null, null);
            
            return Result.success(productId);
        } catch (Exception e) {
            log.error("增加商品浏览次数失败，商品ID: {}", productId, e);
            return Result.error("增加商品浏览次数失败");
        }
    }

    @Override
    public Result<String> incrementFavoriteCount(String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            // 记录收藏互动（MongoDB）
            productInteractionService.recordInteraction(productId, null, "FAVORITE", null, null, null);
            
            return Result.success(productId);
        } catch (Exception e) {
            log.error("增加商品收藏次数失败，商品ID: {}", productId, e);
            return Result.error("增加商品收藏次数失败");
        }
    }

    @Override
    public Result<String> setRecommendedStatus(String productId, Boolean isRecommended) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (isRecommended == null) {
                return Result.error("推荐状态不能为空");
            }
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            product.setIsRecommended(isRecommended);
            product.setUpdateTime(LocalDateTime.now());
            Product updatedProduct = productRepository.save(product);
            
            // 清除缓存
            redisTemplate.delete("product:" + productId);
            
            return Result.success(updatedProduct.getId());
        } catch (Exception e) {
            log.error("设置商品推荐状态失败，商品ID: {}, 状态: {}", productId, isRecommended, e);
            return Result.error("设置商品推荐状态失败");
        }
    }

    @Override
    public Result<String> setNewStatus(String productId, Boolean isNew) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (isNew == null) {
                return Result.error("新品状态不能为空");
            }
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            product.setIsNew(isNew);
            product.setUpdateTime(LocalDateTime.now());
            Product updatedProduct = productRepository.save(product);
            
            // 清除缓存
            redisTemplate.delete("product:" + productId);
            
            return Result.success(updatedProduct.getId());
        } catch (Exception e) {
            log.error("设置商品新品状态失败，商品ID: {}, 状态: {}", productId, isNew, e);
            return Result.error("设置商品新品状态失败");
        }
    }

    @Override
    public Result<String> setHotStatus(String productId, Boolean isHot) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (isHot == null) {
                return Result.error("热销状态不能为空");
            }
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            product.setIsHot(isHot);
            product.setUpdateTime(LocalDateTime.now());
            Product updatedProduct = productRepository.save(product);
            
            // 清除缓存
            redisTemplate.delete("product:" + productId);
            
            return Result.success(updatedProduct.getId());
        } catch (Exception e) {
            log.error("设置商品热销状态失败，商品ID: {}, 状态: {}", productId, isHot, e);
            return Result.error("设置商品热销状态失败");
        }
    }

    @Override
    public Result<String> updateSortWeight(String productId, Integer sortWeight) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (sortWeight == null || sortWeight < 0) {
                return Result.error("排序权重不能为负数");
            }
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            product.setSortWeight(sortWeight);
            product.setUpdateTime(LocalDateTime.now());
            Product updatedProduct = productRepository.save(product);
            
            // 清除缓存
            redisTemplate.delete("product:" + productId);
            
            return Result.success(updatedProduct.getId());
        } catch (Exception e) {
            log.error("更新商品排序权重失败，商品ID: {}, 权重: {}", productId, sortWeight, e);
            return Result.error("更新商品排序权重失败");
        }
    }

    @Override
    public Result<Product> getProductDetail(ProductQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String id = request.getId();
            if (id == null || id.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            // 先从缓存中获取
            String cacheKey = "product:" + id;
            String cachedProduct = redisTemplate.opsForValue().get(cacheKey);
            if (cachedProduct != null) {
                Product product = JSON.parseObject(cachedProduct, Product.class);
                
                // 记录浏览互动（MongoDB）
                productInteractionService.recordInteraction(id, request.getUserId(), "VIEW", 
                        request.getClientIp(), request.getUserAgent(), request.getSessionId());
                
                return Result.success(product);
            }
            
            // 缓存中没有，从数据库获取
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            // 记录浏览互动（MongoDB）
            productInteractionService.recordInteraction(id, request.getUserId(), "VIEW", 
                    request.getClientIp(), request.getUserAgent(), request.getSessionId());
            
            // 存入缓存
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(product), Duration.ofHours(1));
            
            return Result.success(product);
        } catch (Exception e) {
            log.error("获取商品详情失败，请求参数: {}", request, e);
            return Result.error("获取商品详情失败");
        }
    }
}