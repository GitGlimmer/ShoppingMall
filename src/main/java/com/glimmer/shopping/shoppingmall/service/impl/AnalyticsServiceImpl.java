package com.glimmer.shopping.shoppingmall.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.service.AnalyticsService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Glimmer
 */
@Service
@Slf4j
@DS("clickhouse")
public class AnalyticsServiceImpl implements AnalyticsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Result<List<Map<String, Object>>> getDailySales(String startDate, String endDate) {
        try {
            String sql = "SELECT sale_date, SUM(amount) total FROM daily_sales WHERE sale_date BETWEEN ? AND ? GROUP BY sale_date";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, startDate, endDate);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取销售数据失败，开始日期: {}, 结束日期: {}", startDate, endDate, e);
            return Result.error("获取销售数据失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> executeQuery(String sql) {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            return Result.success(result);
        } catch (Exception e) {
            log.error("执行分析查询失败，SQL: {}", sql, e);
            return Result.error("执行分析查询失败");
        }
    }
}