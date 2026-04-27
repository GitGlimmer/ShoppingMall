package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.dto.SalesQueryRequest;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;
import java.util.Map;

/**
 * @author Glimmer
 */
public interface AnalyticsService {
    
    /**
     * 获取销售数据
     *
     * @param request 销售查询请求
     * @return
     */
    Result<List<Map<String, Object>>> getSales(SalesQueryRequest request);

    /**
     * 执行分析查询
     *
     * @param sql
     * @return
     */
    Result<List<Map<String, Object>>> executeQuery(String sql);
}