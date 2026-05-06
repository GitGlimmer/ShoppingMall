package com.glimmer.shopping.shoppingmall.enums;

/**
 * 布尔状态枚举（用于 isHot, isNew, isRecommended 等字段）
 */
public enum BooleanStatusEnum {
    
    YES(1, "是", true),
    NO(0, "否", false);
    
    private final Integer code;
    private final String description;
    private final Boolean value;
    
    BooleanStatusEnum(Integer code, String description, Boolean value) {
        this.code = code;
        this.description = description;
        this.value = value;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Boolean getValue() {
        return value;
    }
    
    public static BooleanStatusEnum fromCode(Integer code) {
        for (BooleanStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return NO;
    }
    
    public static BooleanStatusEnum fromBoolean(Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            return YES;
        }
        return NO;
    }
    
    public static BooleanStatusEnum fromDescription(String description) {
        for (BooleanStatusEnum value : values()) {
            if (value.getDescription().equals(description)) {
                return value;
            }
        }
        return NO;
    }
}