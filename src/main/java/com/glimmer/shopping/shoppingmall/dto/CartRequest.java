package com.glimmer.shopping.shoppingmall.dto;

import lombok.Data;

/**
 * 购物车请求参数
 */
@Data
public class CartRequest {
    
    /** 用户ID */
    private String userId;
    
    /** 商品ID */
    private String productId;
    
    /** 商品数量 */
    private Integer quantity;
}