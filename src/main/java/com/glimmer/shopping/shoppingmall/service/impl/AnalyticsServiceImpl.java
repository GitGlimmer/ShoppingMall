package com.glimmer.shopping.shoppingmall.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.dto.SalesQueryRequest;
import com.glimmer.shopping.shoppingmall.service.AnalyticsService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
    public Result<List<Map<String, Object>>> getSales(SalesQueryRequest request) {
        try {
            if (request == null) {
                return Result.error("请求参数不能为空");
            }
            
            String startDate = request.getStartDate();
            String endDate = request.getEndDate();
            
            if (startDate == null || endDate == null) {
                return Result.error("开始日期和结束日期不能为空");
            }
            
            // 模拟销售数据查询
            List<Map<String, Object>> salesData = new ArrayList<>();
            Map<String, Object> day1 = new HashMap<>();
            day1.put("date", "2024-01-01");
            day1.put("sales", 1000);
            day1.put("orders", 50);
            salesData.add(day1);
            
            Map<String, Object> day2 = new HashMap<>();
            day2.put("date", "2024-01-02");
            day2.put("sales", 1200);
            day2.put("orders", 60);
            salesData.add(day2);
            
            return Result.success(salesData);
        } catch (Exception e) {
            log.error("获取销售数据失败，请求参数: {}", request, e);
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