-- 删除MySQL中Product表的多余字段
-- 这些字段已经移动到MongoDB和ClickHouse中

ALTER TABLE product 
DROP COLUMN IF EXISTS rating,
DROP COLUMN IF EXISTS review_count,
DROP COLUMN IF EXISTS view_count,
DROP COLUMN IF EXISTS favorite_count;