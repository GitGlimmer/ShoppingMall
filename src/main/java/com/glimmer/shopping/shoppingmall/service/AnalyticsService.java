package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;
import java.util.Map;

/**
 * @author Glimmer
 */
public interface AnalyticsService {
    
    /**
     * 获取每日销售数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    Result<List<Map<String, Object>>> getDailySales(String startDate, String endDate);

    /**
     * 执行分析查询
     *
     * @param sql
     * @return
     */
    Result<List<Map<String, Object>>> executeQuery(String sql);
}