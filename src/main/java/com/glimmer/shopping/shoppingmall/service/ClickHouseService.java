package com.glimmer.shopping.shoppingmall.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.entity.ProductStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ClickHouseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcTemplate clickHouseJdbcTemplate;

    private volatile boolean tableInitialized = false;

    @PostConstruct
    public void init() {
    }

    private JdbcTemplate getClickHouseJdbcTemplate() {
        if (clickHouseJdbcTemplate == null) {
            clickHouseJdbcTemplate = jdbcTemplate;
        }
        return clickHouseJdbcTemplate;
    }

    @DS("clickhouse")
    public void initTables() {
        try {
            createMinimalTable();
        } catch (Exception e) {
            log.error("初始化ClickHouse表失败: {}", e.getMessage());
        }
    }

    @DS("clickhouse")
    public void initTablesOnStartup() {
        createMinimalTable();
    }

    private void ensureTableInitialized() {
        if (!tableInitialized) {
            synchronized (this) {
                if (!tableInitialized) {
                    try {
                        initTables();
                        tableInitialized = true;
                        log.info("✅ ClickHouse表初始化完成");
                    } catch (Exception e) {
                        log.error("❌ ClickHouse表初始化失败: {}", e.getMessage());
                    }
                }
            }
        }
    }

    private void createMinimalTable() {
        try {
            String checkSql = "SHOW TABLES LIKE 'product_stats'";
            List<String> tables = getClickHouseJdbcTemplate().queryForList(checkSql, String.class);
            if (!tables.isEmpty()) {
                try {
                    String descSql = "DESCRIBE TABLE product_stats";
                    List<String> columns = getClickHouseJdbcTemplate().query(descSql, (rs, rowNum) -> rs.getString(1));
                    if (columns.contains("daily_sales") && columns.contains("daily_revenue")) {
                        log.info("ℹ️ ClickHouse表已存在且结构正确，跳过创建");
                        return;
                    } else {
                        log.warn("⚠️ ClickHouse表结构不匹配，将删除并重建");
                        getClickHouseJdbcTemplate().execute("DROP TABLE product_stats");
                    }
                } catch (Exception e) {
                    log.warn("检查表结构时出错: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("检查表是否存在时出错: {}", e.getMessage());
        }

        String createSql = "CREATE TABLE IF NOT EXISTS product_stats (" +
                "stat_date Date," +
                "product_id String," +
                "product_name String," +
                "category String," +
                "brand String," +
                "total_rating Double," +
                "rating_count Int32," +
                "avg_rating Double," +
                "total_views UInt64," +
                "total_favorites UInt64," +
                "total_likes UInt64," +
                "total_sales Int32," +
                "total_revenue Double," +
                "daily_views UInt64," +
                "daily_favorites UInt64," +
                "daily_likes UInt64," +
                "daily_sales Int32," +
                "daily_revenue Double" +
                ") ENGINE = MergeTree()" +
                "ORDER BY (stat_date, product_id)";

        try {
            getClickHouseJdbcTemplate().execute(createSql);
            log.info("✅ ClickHouse表创建成功");
        } catch (Exception e) {
            log.error("❌ ClickHouse表创建失败: {}", e.getMessage());
        }
    }

    @DS("clickhouse")
    public void upsertProductStats(ProductStats stats) {
        ensureTableInitialized();
        String sql = "INSERT INTO product_stats VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        getClickHouseJdbcTemplate().update(sql,
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
                stats.getDailyRevenue());
    }

    @DS("clickhouse")
    public List<ProductStats> getTopRatedProducts(int limit) {
        ensureTableInitialized();
        String sql = "SELECT * FROM product_stats WHERE stat_date = today() ORDER BY avg_rating DESC LIMIT ?";

        return getClickHouseJdbcTemplate().query(sql, new Object[]{limit}, new RowMapper<ProductStats>() {
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

    @DS("clickhouse")
    public List<ProductStats> getMostViewedProducts(int limit) {
        ensureTableInitialized();
        String sql = "SELECT * FROM product_stats WHERE stat_date = today() ORDER BY daily_views DESC LIMIT ?";

        return getClickHouseJdbcTemplate().query(sql, new Object[]{limit}, new RowMapper<ProductStats>() {
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

    @DS("clickhouse")
    public List<ProductStats> getMostFavoritedProducts(int limit) {
        ensureTableInitialized();
        String sql = "SELECT * FROM product_stats WHERE stat_date = today() ORDER BY daily_favorites DESC LIMIT ?";

        return getClickHouseJdbcTemplate().query(sql, new Object[]{limit}, new RowMapper<ProductStats>() {
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

    @DS("clickhouse")
    public List<ProductStats> getProductStatsHistory(String productId, LocalDate startDate, LocalDate endDate) {
        ensureTableInitialized();
        String sql = "SELECT * FROM product_stats WHERE product_id = ? AND stat_date BETWEEN ? AND ? ORDER BY stat_date";

        return getClickHouseJdbcTemplate().query(sql, new Object[]{productId, startDate, endDate}, new RowMapper<ProductStats>() {
            @Override
            public ProductStats mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductStats stats = new ProductStats();
                stats.setStatDate(rs.getObject("stat_date", LocalDate.class));
                stats.setProductId(rs.getString("product_id"));
                stats.setProductName(rs.getString("product_name"));
                stats.setAvgRating(rs.getDouble("avg_rating"));
                stats.setTotalViews(rs.getLong("total_views"));
                stats.setTotalFavorites(rs.getLong("total_favorites"));
                stats.setTotalSales(rs.getInt("total_sales"));
                return stats;
            }
        });
    }

    @DS("clickhouse")
    public List<ProductStats> getCategorySalesStats(LocalDate statDate) {
        ensureTableInitialized();
        String sql = "SELECT category, SUM(daily_sales) as total_sales, SUM(daily_revenue) as total_revenue " +
                     "FROM product_stats WHERE stat_date = ? GROUP BY category ORDER BY total_revenue DESC";

        return getClickHouseJdbcTemplate().query(sql, new Object[]{statDate}, new RowMapper<ProductStats>() {
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