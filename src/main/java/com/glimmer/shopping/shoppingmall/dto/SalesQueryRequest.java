package com.glimmer.shopping.shoppingmall.dto;

import lombok.Data;

/**
 * 销售查询请求参数
 */
@Data
public class SalesQueryRequest {
    
    /** 开始日期 */
    private String startDate;
    
    /** 结束日期 */
    private String endDate;
}