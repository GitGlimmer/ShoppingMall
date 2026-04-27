package com.glimmer.shopping.shoppingmall.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.entity.ProductStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ClickHouse数据服务
 * 用于存储和查询统计分析数据
 */
@Service
public class ClickHouseService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private JdbcTemplate clickHouseJdbcTemplate;
    
    @PostConstruct
    public void init() {
        clickHouseJdbcTemplate = jdbcTemplate;
        initTables();
    }
    
    /**
     * 初始化ClickHouse表结构
     */
    @DS("clickhouse")
    private void initTables() {
        // 尝试创建最简化的表结构
        createMinimalTable();
    }
    
    /**
     * 创建最简化的表结构
     */
    @DS("clickhouse")
    private void createMinimalTable() {
        // 尝试多种表创建方式
        String[] tableCreationAttempts = {
            // 尝试1: 最简化版本
            "CREATE TABLE IF NOT EXISTS product_stats (" +
                "stat_date Date," +
                "product_id String," +
                "total_views UInt64," +
                "total_favorites UInt64," +
                "total_sales UInt32" +
                ") ENGINE = MergeTree()" +
                "ORDER BY (stat_date, product_id)",
            
            // 尝试2: 不使用IF NOT EXISTS
            "CREATE TABLE product_stats (" +
                "stat_date Date," +
                "product_id String," +
                "total_views UInt64," +
                "total_favorites UInt64," +
                "total_sales UInt32" +
                ") ENGINE = MergeTree()" +
                "ORDER BY (stat_date, product_id)",
            
            // 尝试3: 使用不同的引擎
            "CREATE TABLE IF NOT EXISTS product_stats (" +
                "stat_date Date," +
                "product_id String," +
                "total_views UInt64" +
                ") ENGINE = Log",
            
            // 尝试4: 最基本的表结构
            "CREATE TABLE IF NOT EXISTS product_stats (" +
                "stat_date Date," +
                "product_id String" +
                ") ENGINE = MergeTree()" +
                "ORDER BY (stat_date, product_id)"
        };
        
        for (int i = 0; i < tableCreationAttempts.length; i++) {
            try {
                clickHouseJdbcTemplate.execute(tableCreationAttempts[i]);
                System.out.println("✅ ClickHouse表创建成功 (尝试 " + (i+1) + ")");
                
                // 验证表是否真的创建成功
                String verifySql = "SHOW TABLES LIKE 'product_stats'";
                java.util.List<String> tables = clickHouseJdbcTemplate.queryForList(verifySql, String.class);
                if (!tables.isEmpty()) {
                    System.out.println("✅ 验证成功: product_stats表已存在");
                    return;
                }
            } catch (Exception e) {
                System.err.println("❌ ClickHouse表创建失败 (尝试 " + (i+1) + "): " + e.getMessage());
            }
        }
        
        // 如果所有尝试都失败，创建备用表
        createFallbackTable();
    }
    
    /**
     * 创建备用表
     */
    @DS("clickhouse")
    private void createFallbackTable() {
        try {
            // 尝试创建最简单的表
            String fallbackSql = "CREATE TABLE IF NOT EXISTS product_stats_backup (" +
                "id String," +
                "product_id String," +
                "stat_date String" +
                ") ENGINE = Log";
            
            clickHouseJdbcTemplate.execute(fallbackSql);
            System.out.println("✅ 备用ClickHouse表创建成功");
        } catch (Exception e) {
            System.err.println("❌ 备用ClickHouse表创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 插入或更新商品统计数据
     */
    @DS("clickhouse")
    public void upsertProductStats(ProductStats stats) {
        String sql = "INSERT INTO product_stats VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        clickHouseJdbcTemplate.update(sql, 
                stats.getStatDate(),
                stats.getProductId(),
                stats.getProductName(),
                stats.getCategory(),
                stats.getBrand(),
                stats.getTotalRating(),
                stats.getRatingCount(),
                stats.getAvgRating(),
                stats.getTotalViews(),
                stats.getTotalFavorites(),
                stats.getTotalLikes(),
                stats.getTotalSales(),
                stats.getTotalRevenue(),
                stats.getDailyViews(),
                stats.getDailyFavorites(),
                stats.getDailyLikes(),
                stats.getDailySales(),
                stats.getDailyRevenue(),
                stats.getWeeklyViews(),
                stats.getWeeklyFavorites(),
                stats.getWeeklyLikes(),
                stats.getWeeklySales(),
                stats.getWeeklyRevenue(),
                stats.getMonthlyViews(),
                stats.getMonthlyFavorites(),
                stats.getMonthlyLikes(),
                stats.getMonthlySales(),
                stats.getMonthlyRevenue(),
                stats.getCreateTime(),
                stats.getUpdateTime()
        );
    }
    
    /**
     * 获取商品评分排行榜
     */
    @DS("clickhouse")
    public List<ProductStats> getTopRatedProducts(int limit) {
        String sql = "SELECT * FROM product_stats WHERE stat_date = today() ORDER BY avg_rating DESC LIMIT ?";
        
        return clickHouseJdbcTemplate.query(sql, new Object[]{limit}, new RowMapper<ProductStats>() {
            @Override
            public ProductStats mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductStats stats = new ProductStats();
                stats.setStatDate(rs.getObject("stat_date", LocalDate.class));
                stats.setProductId(rs.getString("product_id"));
                stats.setProductName(rs.getString("product_name"));
                stats.setCategory(rs.getString("category"));
                stats.setBrand(rs.getString("brand"));
                stats.setAvgRating(rs.getDouble("avg_rating"));
                stats.setRatingCount(rs.getInt("rating_count"));
                stats.setTotalViews(rs.getLong("total_views"));
                stats.setTotalFavorites(rs.getLong("total_favorites"));
                return stats;
            }
        });
    }
    
    /**
     * 获取热门浏览商品
     */
    @DS("clickhouse")
    public List<ProductStats> getMostViewedProducts(int limit) {
        String sql = "SELECT * FROM product_stats WHERE stat_date = today() ORDER BY daily_views DESC LIMIT ?";
        
        return clickHouseJdbcTemplate.query(sql, new Object[]{limit}, new RowMapper<ProductStats>() {
            @Override
            public ProductStats mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductStats stats = new ProductStats();
                stats.setStatDate(rs.getObject("stat_date", LocalDate.class));
                stats.setProductId(rs.getString("product_id"));
                stats.setProductName(rs.getString("product_name"));
                stats.setCategory(rs.getString("category"));
                stats.setBrand(rs.getString("brand"));
                stats.setDailyViews(rs.getLong("daily_views"));
                stats.setTotalViews(rs.getLong("total_views"));
                return stats;
            }
        });
    }
    
    /**
     * 获取热门收藏商品
     */
    @DS("clickhouse")
    public List<ProductStats> getMostFavoritedProducts(int limit) {
        String sql = "SELECT * FROM product_stats WHERE stat_date = today() ORDER BY daily_favorites DESC LIMIT ?";
        
        return clickHouseJdbcTemplate.query(sql, new Object[]{limit}, new RowMapper<ProductStats>() {
            @Override
            public ProductStats mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductStats stats = new ProductStats();
                stats.setStatDate(rs.getObject("stat_date", LocalDate.class));
                stats.setProductId(rs.getString("product_id"));
                stats.setProductName(rs.getString("product_name"));
                stats.setCategory(rs.getString("category"));
                stats.setBrand(rs.getString("brand"));
                stats.setDailyFavorites(rs.getLong("daily_favorites"));
                stats.setTotalFavorites(rs.getLong("total_favorites"));
                return stats;
            }
        });
    }
    
    /**
     * 获取商品历史统计数据
     */
    @DS("clickhouse")
    public List<ProductStats> getProductStatsHistory(String productId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM product_stats WHERE product_id = ? AND stat_date BETWEEN ? AND ? ORDER BY stat_date";
        
        return clickHouseJdbcTemplate.query(sql, new Object[]{productId, startDate, endDate}, new RowMapper<ProductStats>() {
            @Override
            public ProductStats mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductStats stats = new ProductStats();
                stats.setStatDate(rs.getObject("stat_date", LocalDate.class));
                stats.setProductId(rs.getString("product_id"));
                stats.setProductName(rs.getString("product_name"));
                stats.setAvgRating(rs.getDouble("avg_rating"));
                stats.setRatingCount(rs.getInt("rating_count"));
                stats.setDailyViews(rs.getLong("daily_views"));
                stats.setDailyFavorites(rs.getLong("daily_favorites"));
                stats.setDailyLikes(rs.getLong("daily_likes"));
                stats.setDailySales(rs.getInt("daily_sales"));
                stats.setDailyRevenue(rs.getDouble("daily_revenue"));
                return stats;
            }
        });
    }
    
    /**
     * 获取分类销售统计
     */
    @DS("clickhouse")
    public List<ProductStats> getCategorySalesStats(LocalDate statDate) {
        String sql = "SELECT category, SUM(daily_sales) as total_sales, SUM(daily_revenue) as total_revenue " +
                     "FROM product_stats WHERE stat_date = ? GROUP BY category ORDER BY total_revenue DESC";
        
        return clickHouseJdbcTemplate.query(sql, new Object[]{statDate}, new RowMapper<ProductStats>() {
            @Override
            public ProductStats mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductStats stats = new ProductStats();
                stats.setCategory(rs.getString("category"));
                stats.setTotalSales(rs.getInt("total_sales"));
                stats.setTotalRevenue(rs.getDouble("total_revenue"));
                return stats;
            }
        });
    }
}