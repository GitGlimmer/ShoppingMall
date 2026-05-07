package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.SalesQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.OrderStatus;
import com.glimmer.shopping.shoppingmall.repository.OrderRepository;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import com.glimmer.shopping.shoppingmall.repository.UserRepository;
import com.glimmer.shopping.shoppingmall.service.AnalyticsService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Api(tags = "数据统计")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public AnalyticsController(AnalyticsService analyticsService,
                              ProductRepository productRepository,
                              OrderRepository orderRepository,
                              UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @ApiOperation("获取销售数据")
    @PostMapping("/sales")
    public Result<List<Map<String, Object>>> getSales(@RequestBody SalesQueryRequest request) {
        return analyticsService.getSales(request);
    }

    @ApiOperation("获取分类销售占比")
    @GetMapping("/sales/category")
    public Result<List<Map<String, Object>>> getCategorySalesRatio() {
        List<Map<String, Object>> categorySales = productRepository.findCategorySalesStats();
        
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Map<String, Object> item : categorySales) {
            Object salesObj = item.get("total_sales");
            if (salesObj instanceof BigDecimal) {
                totalSales = totalSales.add((BigDecimal) salesObj);
            } else if (salesObj instanceof Number) {
                totalSales = totalSales.add(BigDecimal.valueOf(((Number) salesObj).doubleValue()));
            }
        }
        
        List<Map<String, Object>> filteredResult = new ArrayList<>();
        BigDecimal otherSales = BigDecimal.ZERO;
        
        for (Map<String, Object> item : categorySales) {
            String category = (String) item.get("category");
            Object salesObj = item.get("total_sales");
            
            BigDecimal sales = BigDecimal.ZERO;
            if (salesObj instanceof BigDecimal) {
                sales = (BigDecimal) salesObj;
            } else if (salesObj instanceof Number) {
                sales = BigDecimal.valueOf(((Number) salesObj).doubleValue());
            }
            
            if (sales.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            
            if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = sales.divide(totalSales, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                
                if (ratio.compareTo(BigDecimal.valueOf(3)) < 0) {
                    otherSales = otherSales.add(sales);
                } else {
                    Map<String, Object> ratioItem = new HashMap<>();
                    ratioItem.put("category", category);
                    ratioItem.put("sales", sales.longValue());
                    ratioItem.put("ratio", ratio.setScale(2, RoundingMode.HALF_UP).doubleValue());
                    filteredResult.add(ratioItem);
                }
            }
        }
        
        if (otherSales.compareTo(BigDecimal.ZERO) > 0 && totalSales.compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> otherItem = new HashMap<>();
            BigDecimal otherRatio = otherSales.divide(totalSales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            otherItem.put("category", "其他");
            otherItem.put("sales", otherSales.longValue());
            otherItem.put("ratio", otherRatio.setScale(2, RoundingMode.HALF_UP).doubleValue());
            filteredResult.add(otherItem);
        }
        
        filteredResult.sort((a, b) -> {
            Double ratioA = (Double) a.get("ratio");
            Double ratioB = (Double) b.get("ratio");
            return ratioB.compareTo(ratioA);
        });
        
        return Result.success(filteredResult);
    }

    @ApiOperation("获取数据概览统计")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        long productCount = productRepository.count();
        overview.put("productCount", productCount);
        
        long orderCount = orderRepository.count();
        overview.put("orderCount", orderCount);
        
        long userCount = userRepository.count();
        overview.put("userCount", userCount);
        
        BigDecimal totalSales = productRepository.sumAllSales();
        long salesValue = totalSales != null ? totalSales.longValue() : 0;
        overview.put("totalSales", salesValue);
        
        overview.put("productTrend", productCount > 0 ? 5 : 0);
        overview.put("orderTrend", orderCount > 0 ? 12 : 0);
        overview.put("salesTrend", salesValue > 0 ? 15 : 0);
        overview.put("userTrend", userCount > 0 ? 8 : 0);
        
        return Result.success(overview);
    }

    @ApiOperation("获取实时数据概览")
    @GetMapping("/realtime")
    public Result<Map<String, Object>> getRealtimeData() {
        Map<String, Object> realtime = new HashMap<>();
        
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long onlineUsers = userRepository.countActiveUsers(oneHourAgo);
        realtime.put("onlineUsers", onlineUsers);
        
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(2);
        
        List<Integer> activeStatusList = Arrays.asList(
                OrderStatus.PENDING_PAYMENT.getCode(),
                OrderStatus.PAID.getCode()
        );
        
        long activeOrders = orderRepository.countByStatusInAndCreateTimeBetween(
                activeStatusList, startTime, endTime);
        realtime.put("activeOrders", activeOrders);
        
        long paidBuyers = orderRepository.countDistinctBuyersByStatusAndCreateTimeBetween(
                OrderStatus.PAID.getCode(), startTime, endTime);
        realtime.put("paidBuyers", paidBuyers);
        
        BigDecimal conversionRate = BigDecimal.ZERO;
        if (onlineUsers > 0) {
            conversionRate = BigDecimal.valueOf(paidBuyers)
                    .divide(BigDecimal.valueOf(onlineUsers), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        realtime.put("conversionRate", conversionRate.setScale(2, RoundingMode.HALF_UP).toString());
        
        BigDecimal totalOrderAmount = orderRepository.sumTotalAmountByStatusInAndCreateTimeBetween(
                activeStatusList, startTime, endTime);
        
        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (onlineUsers > 0) {
            avgOrderValue = totalOrderAmount
                    .divide(BigDecimal.valueOf(onlineUsers), 2, RoundingMode.HALF_UP);
        }
        realtime.put("avgOrderValue", avgOrderValue.doubleValue());
        
        return Result.success(realtime);
    }

    @ApiOperation("获取热门商品TOP10")
    @GetMapping("/hotProducts")
    public Result<List<Map<String, Object>>> getHotProducts() {
        List<Map<String, Object>> hotProducts = productRepository.findHotProductsTop10();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> product : hotProducts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", product.get("id"));
            item.put("name", product.get("name"));
            item.put("sales", product.get("sales"));
            item.put("revenue", product.get("revenue"));
            result.add(item);
        }
        
        return Result.success(result);
    }

    @ApiOperation("获取销售趋势数据")
    @GetMapping("/salesTrend")
    public Result<List<Map<String, Object>>> getSalesTrend(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "7") int days) {
        
        List<Map<String, Object>> trend = new ArrayList<>();
        
        if ("monthly".equals(period)) {
            trend = getMonthlySalesTrend(days);
        } else if ("yearly".equals(period)) {
            trend = getYearlySalesTrend(days);
        } else {
            trend = getDailySalesTrend(days);
        }
        
        return Result.success(trend);
    }
    
    private List<Map<String, Object>> getDailySalesTrend(int days) {
        LocalDate today = LocalDate.now();
        
        LocalDate startDate = today.minusDays(days - 1);
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = today.atTime(23, 59, 59);
        
        List<Integer> statusList = Arrays.asList(OrderStatus.PAID.getCode(), OrderStatus.COMPLETED.getCode());
        List<Map<String, Object>> dbResult = orderRepository.getDailySalesStats(statusList, startTime, endTime);
        
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        for (Map<String, Object> item : dbResult) {
            String dateStr = item.get("date").toString();
            resultMap.put(dateStr, item);
        }
        
        List<Map<String, Object>> trend = new ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.toString();
            Map<String, Object> dbItem = resultMap.get(dateStr);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("day", date.getDayOfMonth() + "号");
            
            if (dbItem != null) {
                Object orderCountObj = dbItem.get("orderCount");
                Object revenueObj = dbItem.get("revenue");
                
                if (orderCountObj instanceof Number) {
                    item.put("sales", ((Number) orderCountObj).longValue());
                } else {
                    item.put("sales", 0);
                }
                
                if (revenueObj instanceof BigDecimal) {
                    item.put("revenue", ((BigDecimal) revenueObj).longValue());
                } else if (revenueObj instanceof Number) {
                    item.put("revenue", ((Number) revenueObj).longValue());
                } else {
                    item.put("revenue", 0L);
                }
            } else {
                item.put("sales", 0);
                item.put("revenue", 0L);
            }
            
            trend.add(item);
        }
        
        return trend;
    }
    
    private List<Map<String, Object>> getMonthlySalesTrend(int months) {
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.minusMonths(months).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endTime = today.atTime(23, 59, 59);
        
        List<Integer> statusList = Arrays.asList(OrderStatus.PAID.getCode(), OrderStatus.COMPLETED.getCode());
        List<Map<String, Object>> dbResult = orderRepository.getMonthlySalesStats(statusList, startTime, endTime);
        
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        for (Map<String, Object> item : dbResult) {
            String dateStr = item.get("date").toString();
            resultMap.put(dateStr, item);
        }
        
        List<Map<String, Object>> trend = new ArrayList<>();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate date = today.minusMonths(i);
            String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            Map<String, Object> dbItem = resultMap.get(dateStr);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("label", date.getMonthValue() + "月");
            
            if (dbItem != null) {
                item.put("sales", dbItem.get("orderCount"));
                item.put("revenue", ((BigDecimal) dbItem.get("revenue")).longValue());
            } else {
                item.put("sales", 0);
                item.put("revenue", 0L);
            }
            
            trend.add(item);
        }
        
        return trend;
    }
    
    private List<Map<String, Object>> getYearlySalesTrend(int years) {
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.minusYears(years).withDayOfYear(1).atStartOfDay();
        LocalDateTime endTime = today.atTime(23, 59, 59);
        
        List<Integer> statusList = Arrays.asList(OrderStatus.PAID.getCode(), OrderStatus.COMPLETED.getCode());
        List<Map<String, Object>> dbResult = orderRepository.getYearlySalesStats(statusList, startTime, endTime);
        
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        for (Map<String, Object> item : dbResult) {
            String dateStr = item.get("date").toString();
            resultMap.put(dateStr, item);
        }
        
        List<Map<String, Object>> trend = new ArrayList<>();
        
        for (int i = years - 1; i >= 0; i--) {
            int year = today.getYear() - i;
            String dateStr = String.valueOf(year);
            Map<String, Object> dbItem = resultMap.get(dateStr);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("label", year + "年");
            
            if (dbItem != null) {
                item.put("sales", dbItem.get("orderCount"));
                item.put("revenue", ((BigDecimal) dbItem.get("revenue")).longValue());
            } else {
                item.put("sales", 0);
                item.put("revenue", 0L);
            }
            
            trend.add(item);
        }
        
        return trend;
    }
    
    @ApiOperation("获取销售预警数据")
    @GetMapping("/alerts")
    public Result<List<Map<String, Object>>> getSalesAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        Map<String, Object> lowStockAlert = new HashMap<>();
        lowStockAlert.put("id", 1);
        lowStockAlert.put("type", "warning");
        lowStockAlert.put("icon", "⚠️");
        lowStockAlert.put("title", "库存预警");
        List<Map<String, Object>> lowStockProducts = productRepository.findLowStockProducts(100);
        lowStockAlert.put("items", lowStockProducts);
        alerts.add(lowStockAlert);
        
        Map<String, Object> hotAlert = new HashMap<>();
        hotAlert.put("id", 2);
        hotAlert.put("type", "success");
        hotAlert.put("icon", "🔥");
        hotAlert.put("title", "热销提醒");
        List<Map<String, Object>> weeklyTopSelling = productRepository.findWeeklyTopSellingNewProducts();
        hotAlert.put("items", weeklyTopSelling);
        alerts.add(hotAlert);
        
        Map<String, Object> newAlert = new HashMap<>();
        newAlert.put("id", 3);
        newAlert.put("type", "info");
        newAlert.put("icon", "✨");
        newAlert.put("title", "新品上架");
        List<Map<String, Object>> newProducts = productRepository.findNewProductsInWeek();
        newAlert.put("items", newProducts);
        alerts.add(newAlert);
        
        return Result.success(alerts);
    }
}
