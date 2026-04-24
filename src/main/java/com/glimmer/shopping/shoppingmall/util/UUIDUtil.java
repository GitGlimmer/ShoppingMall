package com.glimmer.shopping.shoppingmall.util;

import java.util.UUID;

/**
 * UUID工具类
 * @author Glimmer
 */
public class UUIDUtil {
    
    /**
     * 生成UUID字符串（不带横线）
     * @return UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成带横线的标准UUID字符串
     * @return 标准UUID字符串
     */
    public static String generateStandardUUID() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 验证UUID格式是否合法
     * @param uuid UUID字符串
     * @return 是否合法
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}