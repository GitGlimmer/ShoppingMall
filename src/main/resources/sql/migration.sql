-- 数据库迁移脚本
-- 用于将数据存储架构从单一MySQL数据库迁移到多数据源架构

-- =============================================
-- 1. MySQL数据库修改（核心数据）
-- =============================================

-- 检查并删除Product表中多余的字段
-- 这些字段已经移动到MongoDB和ClickHouse中
ALTER TABLE product 
DROP COLUMN IF EXISTS rating,
DROP COLUMN IF EXISTS review_count,
DROP COLUMN IF EXISTS view_count,
DROP COLUMN IF EXISTS favorite_count;

-- 验证表结构
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'product' AND TABLE_SCHEMA = DATABASE();

-- =============================================
-- 2. MongoDB集合创建（快速修改数据）
-- =============================================

-- MongoDB集合会自动创建，但我们可以创建索引来提高性能
-- 这些命令需要在MongoDB客户端中执行

-- 创建product_interactions集合的索引
-- db.product_interactions.createIndex({ "product_id": 1 })
-- db.product_interactions.createIndex({ "user_id": 1 })
-- db.product_interactions.createIndex({ "interaction_type": 1 })
-- db.product_interactions.createIndex({ "interaction_time": -1 })
-- db.product_interactions.createIndex({ "product_id": 1, "interaction_type": 1 })

-- 创建product_ratings集合的索引
-- db.product_ratings.createIndex({ "product_id": 1 })
-- db.product_ratings.createIndex({ "user_id": 1 })
-- db.product_ratings.createIndex({ "rating_time": -1 })
-- db.product_ratings.createIndex({ "product_id": 1, "user_id": 1 }, { unique: true })

-- =============================================
-- 3. ClickHouse表创建（统计分析数据）
-- =============================================

-- ClickHouse表会在应用启动时自动创建
-- 如果需要手动创建，可以使用以下SQL：

-- CREATE TABLE IF NOT EXISTS product_stats (
--     stat_date Date,
--     product_id String,
--     product_name String,
--     category String,
--     brand String,
--     total_rating Float64,
--     rating_count UInt32,
--     avg_rating Float64,
--     total_views UInt64,
--     total_favorites UInt64,
--     total_likes UInt64,
--     total_sales UInt32,
--     total_revenue Float64,
--     daily_views UInt64,
--     daily_favorites UInt64,
--     daily_likes UInt64,
--     daily_sales UInt32,
--     daily_revenue Float64,
--     weekly_views UInt64,
--     weekly_favorites UInt64,
--     weekly_likes UInt64,
--     weekly_sales UInt32,
--     weekly_revenue Float64,
--     monthly_views UInt64,
--     monthly_favorites UInt64,
--     monthly_likes UInt64,
--     monthly_sales UInt32,
--     monthly_revenue Float64,
--     create_time DateTime,
--     update_time DateTime
-- ) ENGINE = MergeTree()
-- PARTITION BY toYYYYMM(stat_date)
-- ORDER BY (stat_date, product_id);

-- =============================================
-- 4. 数据迁移（可选）
-- =============================================

-- 如果需要将现有数据从MySQL迁移到其他数据库，可以使用以下脚本
-- 注意：这需要根据实际业务需求和数据量来决定是否执行

-- 迁移评分数据到MongoDB（示例）
-- INSERT INTO mongodb.product_ratings (product_id, user_id, rating, rating_time)
-- SELECT product_id, 'anonymous_user', rating, NOW() 
-- FROM mysql.product 
-- WHERE rating IS NOT NULL;

-- 迁移统计数据到ClickHouse（示例）
-- INSERT INTO clickhouse.product_stats (stat_date, product_id, total_rating, rating_count)
-- SELECT CURDATE(), id, rating, 1 
-- FROM mysql.product 
-- WHERE rating IS NOT NULL;