package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Glimmer
 */
@Entity
@Table(name = "orders")
@Data
public class Order {
    /** id */
    @Id
    private String id;
    
    /** 用户ID */
    private String userId;
    
    /** 订单总金额 */
    private String totalAmount;
    
    /** 订单状态 */
    private String status;
    
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 保存前自动生成UUID
     */
    @PrePersist
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