package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * 存储订单的基本信息，包括用户ID、商品ID、数量、金额、状态等
 * 支持订单状态流转：待支付→已支付→已发货→已完成/已取消
 * 
 * @author Glimmer
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user_id", columnList = "user_id"),
    @Index(name = "idx_orders_product_id", columnList = "product_id"),
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_create_time", columnList = "create_time")
})
@Data
public class Order {
    
    /** 订单唯一标识ID */
    @Id
    @Column(name = "id", length = 36, nullable = false, columnDefinition = "VARCHAR(36) COMMENT '订单唯一标识ID'")
    private String id;
    
    /** 下单用户ID */
    @Column(name = "user_id", length = 36, nullable = false, columnDefinition = "VARCHAR(36) NOT NULL COMMENT '下单用户ID'")
    private String userId;
    
    /** 商品ID */
    @Column(name = "product_id", length = 36, nullable = false, columnDefinition = "VARCHAR(36) NOT NULL COMMENT '商品ID'")
    private String productId;
    
    /** 购买数量 */
    @Column(name = "quantity", columnDefinition = "INT DEFAULT 1 COMMENT '购买数量'")
    private Integer quantity;
    
    /** 订单总金额（单位：元） */
    @Column(name = "total_amount", precision = 12, scale = 2, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00 COMMENT '订单总金额（单位：元）'")
    private Double totalAmount;
    
    /** 订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消 */
    @Column(name = "status", columnDefinition = "INT DEFAULT 1 COMMENT '订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消'")
    private Integer status;
    
    /** 订单创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间'")
    private LocalDateTime createTime;
    
    /** 订单更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间'")
    private LocalDateTime updateTime;
    
    /**
     * 保存前自动生成UUID并设置默认值
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUIDUtil.generateUUID();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.quantity == null) {
            this.quantity = 1;
        }
        if (this.totalAmount == null) {
            this.totalAmount = 0.0;
        }
        if (this.status == null) {
            this.status = OrderStatus.PENDING_PAYMENT.getCode();
        }
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 获取状态描述
     * 
     * @return 状态描述
     */
    @Transient
    public String getStatusDescription() {
        return OrderStatus.fromCode(this.status).getDescription();
    }
}