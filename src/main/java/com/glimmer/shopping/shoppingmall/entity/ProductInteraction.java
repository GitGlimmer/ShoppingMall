package com.glimmer.shopping.shoppingmall.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 商品互动数据（存储在MongoDB）
 * 存储快速点击修改的数据：浏览、收藏等
 */
@Document(collection = "product_interactions")
@Data
public class ProductInteraction {
    
    /** 主键ID */
    @Id
    private String id;
    
    /** 商品ID */
    @Field("product_id")
    private String productId;
    
    /** 用户ID（可为空，表示匿名用户） */
    @Field("user_id")
    private String userId;
    
    /** 互动类型：VIEW-浏览，FAVORITE-收藏，LIKE-点赞 */
    @Field("interaction_type")
    private String interactionType;
    
    /** 互动时间 */
    @Field("interaction_time")
    private LocalDateTime interactionTime;
    
    /** 客户端IP */
    @Field("client_ip")
    private String clientIp;
    
    /** 用户代理信息 */
    @Field("user_agent")
    private String userAgent;
    
    /** 会话ID */
    @Field("session_id")
    private String sessionId;
    
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
        
        if (this.interactionTime == null) {
            this.interactionTime = LocalDateTime.now();
        }
    }
}