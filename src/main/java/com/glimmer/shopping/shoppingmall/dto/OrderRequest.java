package com.glimmer.shopping.shoppingmall.dto;

import lombok.Data;

/**
 * @author Glimmer
 */
@Data
public class OrderRequest {
    private String productId;
    private Integer quantity;
    // 可以添加更多字段，比如 userId、addressId 等
}