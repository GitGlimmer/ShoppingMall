package com.glimmer.shopping.shoppingmall.dto;

import lombok.Data;

/**
 * 商品查询请求参数
 * 用于封装GET请求的参数，避免在URL中直接显示敏感信息
 */
@Data
public class ProductQueryRequest {
    
    /** 商品ID */
    private String id;
    
    /** 商品分类 */
    private String category;
    
    /** 搜索关键字 */
    private String keyword;
    
    /** 最低价格 */
    private String minPrice;
    
    /** 最高价格 */
    private String maxPrice;
    
    /** 限制数量（用于热门商品查询） */
    private Integer limit;
    
    /** 商品状态 */
    private String status;
    
    /** 商品品牌 */
    private String brand;
    
    /** 商品标签 */
    private String tag;
    
    /** 商品产地 */
    private String origin;
    
    /** 最小评分 */
    private Double minRating;
    
    /** 最大评分 */
    private Double maxRating;
    
    /** 最小浏览数 */
    private Integer minViewCount;
    
    /** 最大浏览数 */
    private Integer maxViewCount;
    
    /** 最小收藏数 */
    private Integer minFavoriteCount;
    
    /** 最大收藏数 */
    private Integer maxFavoriteCount;
    
    /** 是否推荐 */
    private Boolean isRecommended;
    
    /** 是否新品 */
    private Boolean isNew;
    
    /** 是否热销 */
    private Boolean isHot;
    
    /** 排序字段 */
    private String sortBy;
    
    /** 排序方向（ASC/DESC） */
    private String sortDirection;
    
    /** 用户ID（用于互动记录） */
    private String userId;
    
    /** 客户端IP（用于互动记录） */
    private String clientIp;
    
    /** 用户代理信息（用于互动记录） */
    private String userAgent;
    
    /** 会话ID（用于互动记录） */
    private String sessionId;
    
    /** 默认构造方法 */
    public ProductQueryRequest() {
        this.limit = 10; // 默认限制10条
        this.sortDirection = "DESC"; // 默认降序
    }
}