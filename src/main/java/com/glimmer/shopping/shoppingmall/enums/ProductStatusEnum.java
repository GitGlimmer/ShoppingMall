package com.glimmer.shopping.shoppingmall.enums;

/**
 * 商品状态枚举
 */
public enum ProductStatusEnum {
    
    ON_SALE(1, "上架", "ON_SALE"),
    OFF_SALE(0, "下架", "OFF_SALE");
    
    private final Integer code;
    private final String description;
    private final String status;
    
    ProductStatusEnum(Integer code, String description, String status) {
        this.code = code;
        this.description = description;
        this.status = status;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public static ProductStatusEnum fromCode(Integer code) {
        for (ProductStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return OFF_SALE;
    }
    
    public static ProductStatusEnum fromStatus(String status) {
        for (ProductStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return OFF_SALE;
    }
    
    public static ProductStatusEnum fromDescription(String description) {
        for (ProductStatusEnum value : values()) {
            if (value.getDescription().equals(description)) {
                return value;
            }
        }
        return OFF_SALE;
    }
}