package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.entity.ProductInteraction;
import com.glimmer.shopping.shoppingmall.entity.ProductStats;
import com.glimmer.shopping.shoppingmall.repository.ProductInteractionRepository;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据同步服务
 * 定期将MongoDB中的互动数据同步到ClickHouse进行统计分析
 */
@Service
@Slf4j
public class DataSyncService {
    
    @Autowired
    private ProductInteractionRepository productInteractionRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    /**
     * 每天凌晨同步数据到ClickHouse
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void syncDataToClickHouse() {
        try {
            log.info("开始同步数据到ClickHouse");
            
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
            
            // 获取所有商品
            List<Product> allProducts = productRepository.findAll();
            
            for (Product product : allProducts) {
                // 获取商品当天的互动数据
                List<ProductInteraction> todayInteractions = productInteractionRepository
                        .findByProductIdAndInteractionTimeBetween(product.getId(), startOfDay, endOfDay);
                
                // 统计互动数据
                Map<String, Long> interactionStats = calculateInteractionStats(todayInteractions);
                
                // 创建统计数据对象
                ProductStats stats = createProductStats(product, today, interactionStats);
                
                // 保存到ClickHouse
                clickHouseService.upsertProductStats(stats);
            }
            
            log.info("数据同步到ClickHouse完成");
        } catch (Exception e) {
            log.error("同步数据到ClickHouse失败", e);
        }
    }
    
    /**
     * 计算互动统计数据
     */
    private Map<String, Long> calculateInteractionStats(List<ProductInteraction> interactions) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("VIEW", 0L);
        stats.put("FAVORITE", 0L);
        stats.put("LIKE", 0L);
        
        for (ProductInteraction interaction : interactions) {
            String type = interaction.getInteractionType();
            stats.put(type, stats.get(type) + 1);
        }
        
        return stats;
    }
    
    /**
     * 创建商品统计数据对象
     */
    private ProductStats createProductStats(Product product, LocalDate statDate, Map<String, Long> interactionStats) {
        ProductStats stats = new ProductStats();
        stats.setStatDate(statDate);
        stats.setProductId(product.getId());
        stats.setProductName(product.getName());
        stats.setCategory(product.getCategory());
        stats.setBrand(product.getBrand());
        
        // 设置互动数据
        stats.setDailyViews(interactionStats.get("VIEW"));
        stats.setDailyFavorites(interactionStats.get("FAVORITE"));
        stats.setDailyLikes(interactionStats.get("LIKE"));
        
        // 设置默认值（实际应用中应该从订单系统获取）
        stats.setDailySales(0);
        stats.setDailyRevenue(0.0);
        
        stats.setCreateTime(LocalDateTime.now());
        stats.setUpdateTime(LocalDateTime.now());
        
        return stats;
    }
}