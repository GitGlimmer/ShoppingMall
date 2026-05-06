package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 商品评论实体类（MongoDB）
 * 
 * 存储用户对商品的评论记录，包括评分和评论内容
 * 
 * @author Glimmer
 */
@Document(collection = "product_reviews")
@Data
public class ProductReview {
    
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
    
    /** 评论内容 */
    @Field("comment")
    private String comment;
    
    /** 创建时间 */
    @Field("create_time")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Field("update_time")
    private LocalDateTime updateTime;
    
    /**
     * 保存前自动生成UUID并设置默认值
     */
    public void prePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUIDUtil.generateUUID();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        this.updateTime = LocalDateTime.now();
    }
}