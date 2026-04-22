package com.glimmer.shopping.shoppingmall.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@DS("clickhouse")
public class AnalyticsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 假设 ClickHouse 中已创建销售日报表 daily_sales
    public List<Map<String, Object>> getDailySales(String startDate, String endDate) {
        String sql = "SELECT sale_date, SUM(amount) total FROM daily_sales WHERE sale_date BETWEEN ? AND ? GROUP BY sale_date";
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    // 执行任意分析SQL（只读）
    public List<Map<String, Object>> executeQuery(String sql) {
        return jdbcTemplate.queryForList(sql);
    }
}
