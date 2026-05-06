package com.glimmer.shopping.shoppingmall.entity;

/**
 * 订单状态枚举
 * 定义订单的五种状态，使用数字状态码进行标识
 * 
 * @author Glimmer
 */
public enum OrderStatus {
    
    /**
     * 待支付 - 订单创建后等待用户支付
     */
    PENDING_PAYMENT(1, "待支付"),
    
    /**
     * 已支付 - 用户已完成支付
     */
    PAID(2, "已支付"),
    
    /**
     * 已发货 - 商家已发货
     */
    SHIPPED(3, "已发货"),
    
    /**
     * 已完成 - 用户确认收货，订单完成
     */
    COMPLETED(4, "已完成"),
    
    /**
     * 已取消 - 订单被取消
     */
    CANCELLED(5, "已取消");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态描述
     */
    private final String description;
    
    OrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 获取状态码
     * 
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }
    
    /**
     * 获取状态描述
     * 
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据状态码获取枚举值
     * 
     * @param code 状态码
     * @return 对应的枚举值
     */
    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态码: " + code);
    }
    
    /**
     * 根据状态描述获取枚举值
     * 
     * @param description 状态描述
     * @return 对应的枚举值
     */
    public static OrderStatus fromDescription(String description) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态描述: " + description);
    }
}