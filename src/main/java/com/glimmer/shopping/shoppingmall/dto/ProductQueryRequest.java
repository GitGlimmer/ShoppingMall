package com.glimmer.shopping.shoppingmall.dto;

import lombok.Data;

/**
 * 商品查询请求参数
 * 用于封装请求的参数，避免在URL中直接显示敏感信息
 */
@Data
public class ProductQueryRequest {
    
    /** 商品ID */
    private String id;
    
    /** 商品分类名称（保留用于兼容旧接口） */
    private String category;
    
    /** 商品分类ID（新接口使用） */
    private String categoryId;
    
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
    
    /** 商品品牌名称（保留用于兼容旧接口） */
    private String brand;
    
    /** 商品品牌ID（新接口使用） */
    private String brandId;
    
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
    
    /** 用户ID */
    private String userId;
    
    /** 客户端IP */
    private String clientIp;
    
    /** 用户代理信息 */
    private String userAgent;
    
    /** 会话ID */
    private String sessionId;
    
    /** 互动类型（VIEW/FAVORITE/LIKE） */
    private String interactionType;
    
    /** 评分值 */
    private Double rating;
    
    /** 评分评论 */
    private String comment;
    
    /** 是否匿名 */
    private Boolean isAnonymous;
    
    /** 页码 */
    private Integer page = 1;
    
    /** 每页数量 */
    private Integer size = 10;
    
    /** 开始日期 */
    private String startDate;
    
    /** 结束日期 */
    private String endDate;
    
    public ProductQueryRequest() {
        this.limit = 10;
        this.sortDirection = "DESC";
    }
}