package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 支付交易实体类
 * 
 * 存储支付交易的基本信息，包括订单号、金额、支付方式、状态等
 * 该实体对应Oracle数据库的PAYMENT_TRANSACTION表
 * 
 * @author Glimmer
 */
@Entity
@Table(name = "PAYMENT_TRANSACTION", schema = "SHOPPING_MALL")
@Data
public class PaymentTransaction {
    
    /** 支付交易唯一标识ID */
    @Id
    @Column(name = "ID", length = 36, nullable = false)
    private String id;
    
    /** 订单号 */
    @Column(name = "ORDER_NO", length = 100)
    private String orderNo;
    
    /** 支付金额（单位：元） */
    @Column(name = "AMOUNT", precision = 12, scale = 2)
    private Double amount;
    
    /** 支付方式：ALIPAY-支付宝，WECHAT-微信支付，BANK-银行卡 */
    @Column(name = "PAYMENT_METHOD", length = 50)
    private String paymentMethod;
    
    /** 支付状态：1-待支付(PENDING)，2-支付成功(SUCCESS)，3-支付失败(FAILED)，4-已退款(REFUNDED) */
    @Column(name = "STATUS")
    private Integer status;
    
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_TIME")
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
        if (this.amount == null) {
            this.amount = 0.0;
        }
        if (this.status == null) {
            this.status = PaymentStatus.PENDING.getCode();
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
        return PaymentStatus.fromCode(this.status).getDescription();
    }
    
    /**
     * 获取状态字符串
     * 
     * @return 状态字符串
     */
    @Transient
    public String getStatusString() {
        return PaymentStatus.fromCode(this.status).getStatus();
    }
    
    /**
     * 支付状态枚举
     */
    public enum PaymentStatus {
        
        /** 待支付 */
        PENDING(1, "待支付"),
        
        /** 支付成功 */
        SUCCESS(2, "支付成功"),
        
        /** 支付失败 */
        FAILED(3, "支付失败"),
        
        /** 已退款 */
        REFUNDED(4, "已退款");
        
        private final Integer code;
        private final String description;
        
        PaymentStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public Integer getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getStatus() {
            return this.name();
        }
        
        public static PaymentStatus fromCode(Integer code) {
            for (PaymentStatus status : PaymentStatus.values()) {
                if (status.getCode().equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("未知的支付状态码: " + code);
        }
    }
}