package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问接口
 * 
 * 提供订单数据的查询和统计方法
 * 
 * @author Glimmer
 */
public interface OrderRepository extends JpaRepository<Order, String> {
    
    /**
     * 统计指定时间范围内指定状态的订单数量
     * 
     * @param statusList 状态列表（数字状态码）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单数量
     */
    long countByStatusInAndCreateTimeBetween(List<Integer> statusList, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内指定状态的订单总金额
     * 
     * @param statusList 状态列表（数字状态码）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单总金额
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status IN :statusList AND o.createTime BETWEEN :startTime AND :endTime")
    BigDecimal sumTotalAmountByStatusInAndCreateTimeBetween(
            @Param("statusList") List<Integer> statusList,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内已支付订单的买家数量（去重）
     * 
     * @param status 状态码（数字）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 买家数量
     */
    @Query("SELECT COUNT(DISTINCT o.userId) FROM Order o WHERE o.status = :status AND o.createTime BETWEEN :startTime AND :endTime")
    long countDistinctBuyersByStatusAndCreateTimeBetween(
            @Param("status") Integer status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 按日期分组统计销售额（每日）
     * 
     * @param statusList 状态列表（数字状态码）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 每日销售统计
     */
    @Query(value = "SELECT DATE(create_time) as date, COUNT(id) as orderCount, COALESCE(SUM(total_amount), 0) as revenue " +
                   "FROM orders WHERE status IN :statusList AND create_time BETWEEN :startTime AND :endTime " +
                   "GROUP BY DATE(create_time) ORDER BY date", nativeQuery = true)
    List<Map<String, Object>> getDailySalesStats(
            @Param("statusList") List<Integer> statusList,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 按月份分组统计销售额
     * 
     * @param statusList 状态列表（数字状态码）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 每月销售统计
     */
    @Query(value = "SELECT DATE_FORMAT(create_time, '%Y-%m') as date, COUNT(id) as orderCount, COALESCE(SUM(total_amount), 0) as revenue " +
                   "FROM orders WHERE status IN :statusList AND create_time BETWEEN :startTime AND :endTime " +
                   "GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY date", nativeQuery = true)
    List<Map<String, Object>> getMonthlySalesStats(
            @Param("statusList") List<Integer> statusList,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 按年份分组统计销售额
     * 
     * @param statusList 状态列表（数字状态码）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 每年销售统计
     */
    @Query(value = "SELECT YEAR(create_time) as date, COUNT(id) as orderCount, COALESCE(SUM(total_amount), 0) as revenue " +
                   "FROM orders WHERE status IN :statusList AND create_time BETWEEN :startTime AND :endTime " +
                   "GROUP BY YEAR(create_time) ORDER BY date", nativeQuery = true)
    List<Map<String, Object>> getYearlySalesStats(
            @Param("statusList") List<Integer> statusList,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}