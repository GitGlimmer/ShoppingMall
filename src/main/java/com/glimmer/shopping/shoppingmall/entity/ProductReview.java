package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author Glimmer
 */
@Document(collection = "product_reviews")
@Data
public class ProductReview {
    /** id */
    @Id
    private String id;
    
    /** 商品ID */
    private String productId;
    
    /** 用户ID */
    private String userId;
    
    /** 评分 */
    private String rating;
    
    /** 评论内容 */
    private String comment;
    
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 保存前自动生成UUID
     */
    public void prePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUIDUtil.generateUUID();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
    }
}