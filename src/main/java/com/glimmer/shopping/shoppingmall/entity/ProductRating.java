package com.glimmer.shopping.shoppingmall.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 商品评分数据（存储在MongoDB）
 * 存储用户的评分记录
 */
@Document(collection = "product_ratings")
@Data
public class ProductRating {
    
    /** 主键ID */
    @Id
    private String id;
    
    /** 商品ID */
    @Field("product_id")
    private String productId;
    
    /** 用户ID */
    @Field("user_id")
    private String userId;
    
    /** 评分（1-5分） */
    @Field("rating")
    private Double rating;
    
    /** 评分内容/评论 */
    @Field("comment")
    private String comment;
    
    /** 评分时间 */
    @Field("rating_time")
    private LocalDateTime ratingTime;
    
    /** 是否匿名评分 */
    @Field("is_anonymous")
    private Boolean isAnonymous;
    
    /** 客户端IP */
    @Field("client_ip")
    private String clientIp;
    
    /** 用户代理信息 */
    @Field("user_agent")
    private String userAgent;
    
    /** 创建时间 */
    @Field("create_time")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Field("update_time")
    private LocalDateTime updateTime;
    
    /**
     * 保存前设置时间
     */
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        this.updateTime = LocalDateTime.now();
        
        if (this.ratingTime == null) {
            this.ratingTime = LocalDateTime.now();
        }
        
        if (this.isAnonymous == null) {
            this.isAnonymous = false;
        }
    }
}