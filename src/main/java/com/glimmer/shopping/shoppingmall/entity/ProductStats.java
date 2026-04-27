package com.glimmer.shopping.shoppingmall.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品统计数据（存储在ClickHouse）
 * 存储统计分析数据：评分排行榜、热门浏览、热门收藏等
 */
@Data
public class ProductStats {
    
    /** 统计日期 */
    private LocalDate statDate;
    
    /** 商品ID */
    private String productId;
    
    /** 商品名称 */
    private String productName;
    
    /** 商品分类 */
    private String category;
    
    /** 商品品牌 */
    private String brand;
    
    /** 总评分 */
    private Double totalRating;
    
    /** 评分次数 */
    private Integer ratingCount;
    
    /** 平均评分 */
    private Double avgRating;
    
    /** 总浏览数 */
    private Long totalViews;
    
    /** 总收藏数 */
    private Long totalFavorites;
    
    /** 总点赞数 */
    private Long totalLikes;
    
    /** 总销量 */
    private Integer totalSales;
    
    /** 销售额 */
    private Double totalRevenue;
    
    /** 日浏览数 */
    private Long dailyViews;
    
    /** 日收藏数 */
    private Long dailyFavorites;
    
    /** 日点赞数 */
    private Long dailyLikes;
    
    /** 日销量 */
    private Integer dailySales;
    
    /** 日销售额 */
    private Double dailyRevenue;
    
    /** 周浏览数 */
    private Long weeklyViews;
    
    /** 周收藏数 */
    private Long weeklyFavorites;
    
    /** 周点赞数 */
    private Long weeklyLikes;
    
    /** 周销量 */
    private Integer weeklySales;
    
    /** 周销售额 */
    private Double weeklyRevenue;
    
    /** 月浏览数 */
    private Long monthlyViews;
    
    /** 月收藏数 */
    private Long monthlyFavorites;
    
    /** 月点赞数 */
    private Long monthlyLikes;
    
    /** 月销量 */
    private Integer monthlySales;
    
    /** 月销售额 */
    private Double monthlyRevenue;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}