package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.entity.ProductRating;
import com.glimmer.shopping.shoppingmall.repository.ProductRatingRepository;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import com.glimmer.shopping.shoppingmall.service.ProductRatingService;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品评分服务实现
 */
@Service
@Slf4j
public class ProductRatingServiceImpl implements ProductRatingService {
    
    @Autowired
    private ProductRatingRepository productRatingRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Result<String> addOrUpdateRating(String productId, String userId, Double rating, 
                                          String comment, Boolean isAnonymous, String clientIp, String userAgent) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("用户ID不能为空");
            }
            if (rating == null || rating < 0 || rating > 5) {
                return Result.error("评分必须在0-5之间");
            }
            
            // 检查商品是否存在
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            // 检查用户是否已经评分过
            Optional<ProductRating> existingRating = productRatingRepository.findByProductIdAndUserId(productId, userId);
            
            ProductRating productRating;
            if (existingRating.isPresent()) {
                // 更新现有评分
                productRating = existingRating.get();
                productRating.setRating(rating);
                productRating.setComment(comment);
                productRating.setIsAnonymous(isAnonymous);
                productRating.setClientIp(clientIp);
                productRating.setUserAgent(userAgent);
                productRating.setRatingTime(LocalDateTime.now());
                log.info("更新用户评分，商品ID: {}, 用户ID: {}, 原评分: {}, 新评分: {}", 
                        productId, userId, existingRating.get().getRating(), rating);
            } else {
                // 创建新评分
                productRating = new ProductRating();
                productRating.setProductId(productId);
                productRating.setUserId(userId);
                productRating.setRating(rating);
                productRating.setComment(comment);
                productRating.setIsAnonymous(isAnonymous);
                productRating.setClientIp(clientIp);
                productRating.setUserAgent(userAgent);
                productRating.prePersist();
                log.info("新增用户评分，商品ID: {}, 用户ID: {}, 评分: {}", productId, userId, rating);
            }
            
            ProductRating savedRating = productRatingRepository.save(productRating);
            
            return Result.success(savedRating.getId());
        } catch (Exception e) {
            log.error("添加或更新评分失败，商品ID: {}, 用户ID: {}, 评分: {}", productId, userId, rating, e);
            return Result.error("添加或更新评分失败");
        }
    }
    
    @Override
    public Result<Double> getAverageRating(String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            List<ProductRating> ratings = productRatingRepository.findByProductId(productId);
            if (ratings.isEmpty()) {
                return Result.success(0.0);
            }
            
            double average = ratings.stream()
                    .mapToDouble(ProductRating::getRating)
                    .average()
                    .orElse(0.0);
            
            // 保留两位小数
            average = Math.round(average * 100.0) / 100.0;
            
            return Result.success(average);
        } catch (Exception e) {
            log.error("获取平均评分失败，商品ID: {}", productId, e);
            return Result.error("获取平均评分失败");
        }
    }
    
    @Override
    public Result<Map<String, Object>> getRatingStatistics(String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            List<ProductRating> ratings = productRatingRepository.findByProductId(productId);
            
            Map<String, Object> statistics = new HashMap<>();
            
            // 总评分数量
            statistics.put("totalRatings", ratings.size());
            
            // 平均评分
            double average = ratings.stream()
                    .mapToDouble(ProductRating::getRating)
                    .average()
                    .orElse(0.0);
            statistics.put("averageRating", Math.round(average * 100.0) / 100.0);
            
            // 评分分布
            Map<Integer, Long> ratingDistribution = ratings.stream()
                    .collect(Collectors.groupingBy(
                            rating -> rating.getRating().intValue(),
                            Collectors.counting()
                    ));
            
            // 确保所有评分级别都有值
            for (int i = 1; i <= 5; i++) {
                ratingDistribution.putIfAbsent(i, 0L);
            }
            
            statistics.put("ratingDistribution", ratingDistribution);
            
            // 最新评分时间
            Optional<LocalDateTime> latestRatingTime = ratings.stream()
                    .map(ProductRating::getRatingTime)
                    .max(LocalDateTime::compareTo);
            statistics.put("latestRatingTime", latestRatingTime.orElse(null));
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取评分统计失败，商品ID: {}", productId, e);
            return Result.error("获取评分统计失败");
        }
    }
    
    @Override
    public Result<List<ProductRating>> getUserRatings(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("用户ID不能为空");
            }
            
            List<ProductRating> ratings = productRatingRepository.findByUserId(userId);
            
            // 按评分时间倒序排列
            ratings.sort((r1, r2) -> r2.getRatingTime().compareTo(r1.getRatingTime()));
            
            return Result.success(ratings);
        } catch (Exception e) {
            log.error("获取用户评分记录失败，用户ID: {}", userId, e);
            return Result.error("获取用户评分记录失败");
        }
    }
    
    @Override
    public PageResult<ProductRating> getProductRatings(String productId, Integer page, Integer size) {
        if (productId == null || productId.trim().isEmpty()) {
            return PageResult.of(new ArrayList<>(), page, size);
        }
        
        if (page == null || page < 1) page = 1;
        if (size == null || size <= 0) size = 10;
        
        try {
            List<ProductRating> allRatings = productRatingRepository.findByProductId(productId);
            
            allRatings.sort((r1, r2) -> r2.getRatingTime().compareTo(r1.getRatingTime()));
            
            int start = (page - 1) * size;
            int end = Math.min(start + size, allRatings.size());
            
            if (start >= allRatings.size()) {
                return PageResult.of(new ArrayList<>(), 0L, page, size);
            }
            
            List<ProductRating> pagedRatings = allRatings.subList(start, end);
            
            return PageResult.of(pagedRatings, (long) allRatings.size(), page, size);
        } catch (Exception e) {
            log.error("获取商品评分记录失败，商品ID: {}", productId, e);
            return PageResult.of(new ArrayList<>(), page, size);
        }
    }
    
    @Override
    public Result<Boolean> hasUserRated(String productId, String userId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("用户ID不能为空");
            }
            
            Optional<ProductRating> rating = productRatingRepository.findByProductIdAndUserId(productId, userId);
            
            return Result.success(rating.isPresent());
        } catch (Exception e) {
            log.error("检查用户评分状态失败，商品ID: {}, 用户ID: {}", productId, userId, e);
            return Result.error("检查用户评分状态失败");
        }
    }
    
    @Override
    public Result<String> deleteRating(String productId, String userId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("用户ID不能为空");
            }
            
            Optional<ProductRating> rating = productRatingRepository.findByProductIdAndUserId(productId, userId);
            if (rating.isPresent()) {
                productRatingRepository.delete(rating.get());
                log.info("删除用户评分，商品ID: {}, 用户ID: {}", productId, userId);
                return Result.success("评分删除成功");
            } else {
                return Result.error("评分记录不存在");
            }
        } catch (Exception e) {
            log.error("删除评分记录失败，商品ID: {}, 用户ID: {}", productId, userId, e);
            return Result.error("删除评分记录失败");
        }
    }
    
    @Override
    public Result<List<Map<String, Object>>> getTopRatedProducts(Integer limit) {
        try {
            if (limit <= 0) limit = 10;
            
            // 获取所有商品的评分统计
            List<Product> allProducts = productRepository.findAll();
            
            List<Map<String, Object>> topRatedProducts = new ArrayList<>();
            
            for (Product product : allProducts) {
                Result<Map<String, Object>> statisticsResult = getRatingStatistics(product.getId());
                if (statisticsResult.getCode() == 200) {
                    Map<String, Object> stats = statisticsResult.getData();
                    
                    Map<String, Object> productStats = new HashMap<>();
                    productStats.put("productId", product.getId());
                    productStats.put("productName", product.getName());
                    productStats.put("category", product.getCategory());
                    productStats.put("averageRating", stats.get("averageRating"));
                    productStats.put("totalRatings", stats.get("totalRatings"));
                    
                    topRatedProducts.add(productStats);
                }
            }
            
            // 按平均评分和评分数量排序
            topRatedProducts.sort((p1, p2) -> {
                Double rating1 = (Double) p1.get("averageRating");
                Double rating2 = (Double) p2.get("averageRating");
                Integer count1 = (Integer) p1.get("totalRatings");
                Integer count2 = (Integer) p2.get("totalRatings");
                
                // 先按评分排序，评分相同按数量排序
                int ratingCompare = Double.compare(rating2, rating1);
                if (ratingCompare != 0) {
                    return ratingCompare;
                }
                return Integer.compare(count2, count1);
            });
            
            // 限制返回数量
            if (topRatedProducts.size() > limit) {
                topRatedProducts = topRatedProducts.subList(0, limit);
            }
            
            return Result.success(topRatedProducts);
        } catch (Exception e) {
            log.error("获取热门评分商品失败", e);
            return Result.error("获取热门评分商品失败");
        }
    }
    
    @Override
    public Result<List<ProductRating>> getRecentRatings(Integer limit) {
        try {
            if (limit <= 0) limit = 10;
            
            // 获取所有评分记录并按时间排序
            List<ProductRating> allRatings = productRatingRepository.findAll();
            
            // 按评分时间倒序排列
            allRatings.sort((r1, r2) -> r2.getRatingTime().compareTo(r1.getRatingTime()));
            
            // 限制返回数量
            if (allRatings.size() > limit) {
                allRatings = allRatings.subList(0, limit);
            }
            
            return Result.success(allRatings);
        } catch (Exception e) {
            log.error("获取最新评分记录失败", e);
            return Result.error("获取最新评分记录失败");
        }
    }
}