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
@Table(name = "PAYMENT_TRANSACTION")
@Data
public class PaymentTransaction {
    /** id */
    @Id
    private String id;
    
    /** 订单号 */
    private String orderNo;
    
    /** 支付金额 */
    private String amount;
    
    /** 支付方式 */
    private String paymentMethod;
    
    /** 支付状态 */
    private String status;
    
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
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
    }
}