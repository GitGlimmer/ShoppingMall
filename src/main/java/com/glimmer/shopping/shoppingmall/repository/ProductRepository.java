package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Glimmer
 */
public interface ProductRepository extends JpaRepository<Product, String> {
    
    /**
     * 按分类查询商品
     */
    List<Product> findByCategory(String category);
    
    /**
     * 按状态查询商品（1=上架, 0=下架）
     */
    List<Product> findByStatus(Integer status);
    
    /**
     * 按商品名称模糊查询
     */
    List<Product> findByNameContaining(String name);
    
    /**
     * 按价格范围查询商品
     */
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    /**
     * 按销量降序查询商品
     */
    List<Product> findByOrderBySalesDesc();
    
    /**
     * 查询热门商品（按销量排序，限制数量）
     */
    @Query(value = "SELECT * FROM product WHERE status = 1 ORDER BY sales DESC LIMIT :limit", nativeQuery = true)
    List<Product> findHotProducts(@Param("limit") int limit);
    
    /**
     * 统计商品总数
     */
    @Query("SELECT COUNT(p) FROM Product p")
    Long countAllProducts();
    
    /**
     * 统计上架商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 1")
    Long countOnSaleProducts();
    
    /**
     * 统计下架商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 0")
    Long countOffSaleProducts();
    
    /**
     * 按品牌查询商品
     */
    List<Product> findByBrand(String brand);
    
    /**
     * 按是否推荐查询商品（1=是）
     */
    @Query("SELECT p FROM Product p WHERE p.isRecommended = 1")
    List<Product> findByIsRecommendedTrue();
    
    /**
     * 按是否新品查询商品（1=是）
     */
    @Query("SELECT p FROM Product p WHERE p.isNew = 1")
    List<Product> findByIsNewTrue();
    
    /**
     * 按是否热销查询商品（1=是）
     */
    @Query("SELECT p FROM Product p WHERE p.isHot = 1")
    List<Product> findByIsHotTrue();
    
    /**
     * 按创建时间降序查询商品
     */
    List<Product> findByOrderByCreateTimeDesc();
    
    /**
     * 按标签查询商品（模糊匹配）
     */
    @Query("SELECT p FROM Product p WHERE p.tags LIKE %:tag%")
    List<Product> findByTag(@Param("tag") String tag);
    
    /**
     * 按产地查询商品
     */
    List<Product> findByOrigin(String origin);
    
    /**
     * 按排序权重降序查询商品
     */
    List<Product> findByOrderBySortWeightDesc();
    
    /**
     * 统计推荐商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isRecommended = 1")
    Long countRecommendedProducts();
    
    /**
     * 统计新品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isNew = 1")
    Long countNewProducts();
    
    /**
     * 统计热销商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isHot = 1")
    Long countHotProducts();
    
    /**
     * 统计总销售额
     */
    @Query(value = "SELECT COALESCE(SUM(sales * price), 0) FROM product", nativeQuery = true)
    BigDecimal sumAllSales();
    
    /**
     * 统计总销量
     */
    @Query(value = "SELECT COALESCE(SUM(sales), 0) FROM product", nativeQuery = true)
    Long sumAllQuantity();
    
    /**
     * 查询热门商品TOP10（按销量降序，无销量按名称排序）
     */
    @Query(value = "SELECT id, name, sales, COALESCE(sales * price, 0) as revenue, price " +
                   "FROM product WHERE status = 1 " +
                   "ORDER BY sales DESC, name ASC LIMIT 10", nativeQuery = true)
    List<Map<String, Object>> findHotProductsTop10();
    
    /**
     * 查询库存不足的商品（库存低于指定阈值）
     */
    @Query(value = "SELECT id, name, stock FROM product WHERE stock IS NOT NULL AND stock < :threshold ORDER BY stock ASC", nativeQuery = true)
    List<Map<String, Object>> findLowStockProducts(@Param("threshold") int threshold);
    
    /**
     * 查询今日新增商品数量
     */
    @Query(value = "SELECT COUNT(*) FROM product WHERE DATE(create_time) = CURDATE()", nativeQuery = true)
    Long countTodayNewProducts();
    
    /**
     * 查询一周内销量最高的商品
     */
    @Query(value = "SELECT p.id, p.name, SUM(o.quantity) as total_sales " +
                   "FROM orders o JOIN product p ON o.product_id = p.id " +
                   "WHERE o.status IN (2, 4) " +
                   "AND o.create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                   "GROUP BY o.product_id, p.id, p.name " +
                   "ORDER BY total_sales DESC LIMIT 1", nativeQuery = true)
    List<Map<String, Object>> findWeeklyTopSellingProduct();
    
    /**
     * 查询一周内创建的新品（新品上架）
     */
    @Query(value = "SELECT id, name, create_time, stock, price FROM product " +
                   "WHERE status = 1 " +
                   "AND create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                   "ORDER BY create_time DESC", nativeQuery = true)
    List<Map<String, Object>> findNewProductsInWeek();
    
    /**
     * 查询一周内创建且销量最高的前三商品（热销提醒）
     */
    @Query(value = "SELECT id, name, sales as total_sales, price FROM product " +
                   "WHERE status = 1 " +
                   "AND create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                   "ORDER BY sales DESC LIMIT 3", nativeQuery = true)
    List<Map<String, Object>> findWeeklyTopSellingNewProducts();
    
    /**
     * 分页查询商品（支持多条件筛选）
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.brand b LEFT JOIN p.category c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%) AND " +
           "(:categoryId IS NULL OR :categoryId = '' OR p.categoryId = :categoryId) AND " +
           "(:brandId IS NULL OR :brandId = '' OR p.brandId = :brandId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.createTime >= :startDate) AND " +
           "(:endDate IS NULL OR p.createTime <= :endDate)")
    Page<Product> findByConditions(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("status") Integer status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * 分页查询商品（支持品牌名称升序排序）- 中英文混合按首字母排序
     */
    @Query(value = "SELECT p.* FROM product p LEFT JOIN brand b ON p.brand_id = b.id LEFT JOIN category c ON p.category_id = c.id WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR p.name LIKE CONCAT('%', :keyword, '%')) AND " +
           "(:categoryId IS NULL OR :categoryId = '' OR p.category_id = :categoryId) AND " +
           "(:brandId IS NULL OR :brandId = '' OR p.brand_id = :brandId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.create_time >= :startDate) AND " +
           "(:endDate IS NULL OR p.create_time <= :endDate) " +
           "ORDER BY IFNULL(ELT(INTERVAL(CONV(HEX(LEFT(CONVERT(COALESCE(b.name, '') USING gbk),1)),16,10), " +
           "0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7,0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,0xC8F6,0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1), " +
           "'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P','Q','R','S','T','W','X','Y','Z'), UPPER(LEFT(COALESCE(b.name, ''),1))) ASC, " +
           "CONVERT(COALESCE(b.name, '') USING gbk) COLLATE gbk_chinese_ci ASC LIMIT :offset, :size", nativeQuery = true)
    List<Product> findByConditionsOrderByBrandNameAsc(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("status") Integer status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("offset") int offset,
            @Param("size") int size);
    
    /**
     * 分页查询商品（支持品牌名称降序排序）- 中英文混合按首字母排序
     */
    @Query(value = "SELECT p.* FROM product p LEFT JOIN brand b ON p.brand_id = b.id LEFT JOIN category c ON p.category_id = c.id WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR p.name LIKE CONCAT('%', :keyword, '%')) AND " +
           "(:categoryId IS NULL OR :categoryId = '' OR p.category_id = :categoryId) AND " +
           "(:brandId IS NULL OR :brandId = '' OR p.brand_id = :brandId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.create_time >= :startDate) AND " +
           "(:endDate IS NULL OR p.create_time <= :endDate) " +
           "ORDER BY IFNULL(ELT(INTERVAL(CONV(HEX(LEFT(CONVERT(COALESCE(b.name, '') USING gbk),1)),16,10), " +
           "0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7,0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,0xC8F6,0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1), " +
           "'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P','Q','R','S','T','W','X','Y','Z'), UPPER(LEFT(COALESCE(b.name, ''),1))) DESC, " +
           "CONVERT(COALESCE(b.name, '') USING gbk) COLLATE gbk_chinese_ci DESC LIMIT :offset, :size", nativeQuery = true)
    List<Product> findByConditionsOrderByBrandNameDesc(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("status") Integer status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("offset") int offset,
            @Param("size") int size);
    
    /**
     * 分页查询商品（支持商品名称升序排序）- 中英文混合按首字母排序
     */
    @Query(value = "SELECT p.* FROM product p LEFT JOIN brand b ON p.brand_id = b.id LEFT JOIN category c ON p.category_id = c.id WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR p.name LIKE CONCAT('%', :keyword, '%')) AND " +
           "(:categoryId IS NULL OR :categoryId = '' OR p.category_id = :categoryId) AND " +
           "(:brandId IS NULL OR :brandId = '' OR p.brand_id = :brandId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.create_time >= :startDate) AND " +
           "(:endDate IS NULL OR p.create_time <= :endDate) " +
           "ORDER BY IFNULL(ELT(INTERVAL(CONV(HEX(LEFT(CONVERT(p.name USING gbk),1)),16,10), " +
           "0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7,0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,0xC8F6,0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1), " +
           "'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P','Q','R','S','T','W','X','Y','Z'), UPPER(LEFT(p.name,1))) ASC, " +
           "CONVERT(p.name USING gbk) COLLATE gbk_chinese_ci ASC LIMIT :offset, :size", nativeQuery = true)
    List<Product> findByConditionsOrderByNameAsc(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("status") Integer status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("offset") int offset,
            @Param("size") int size);
    
    /**
     * 分页查询商品（支持商品名称降序排序）- 中英文混合按首字母排序
     */
    @Query(value = "SELECT p.* FROM product p LEFT JOIN brand b ON p.brand_id = b.id LEFT JOIN category c ON p.category_id = c.id WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR p.name LIKE CONCAT('%', :keyword, '%')) AND " +
           "(:categoryId IS NULL OR :categoryId = '' OR p.category_id = :categoryId) AND " +
           "(:brandId IS NULL OR :brandId = '' OR p.brand_id = :brandId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.create_time >= :startDate) AND " +
           "(:endDate IS NULL OR p.create_time <= :endDate) " +
           "ORDER BY IFNULL(ELT(INTERVAL(CONV(HEX(LEFT(CONVERT(p.name USING gbk),1)),16,10), " +
           "0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7,0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,0xC8F6,0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1), " +
           "'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P','Q','R','S','T','W','X','Y','Z'), UPPER(LEFT(p.name,1))) DESC, " +
           "CONVERT(p.name USING gbk) COLLATE gbk_chinese_ci DESC LIMIT :offset, :size", nativeQuery = true)
    List<Product> findByConditionsOrderByNameDesc(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("status") Integer status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("offset") int offset,
            @Param("size") int size);
    
    /**
     * 查询符合条件的商品总数（用于排序分页）
     */
    @Query(value = "SELECT COUNT(*) FROM product p LEFT JOIN brand b ON p.brand_id = b.id LEFT JOIN category c ON p.category_id = c.id WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR p.name LIKE CONCAT('%', :keyword, '%')) AND " +
           "(:categoryId IS NULL OR :categoryId = '' OR p.category_id = :categoryId) AND " +
           "(:brandId IS NULL OR :brandId = '' OR p.brand_id = :brandId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.create_time >= :startDate) AND " +
           "(:endDate IS NULL OR p.create_time <= :endDate)", nativeQuery = true)
    Long countByConditionsForSort(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("status") Integer status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查询评分最高的商品
     */
    @Query(value = "SELECT * FROM product WHERE status = 1 ORDER BY sales DESC LIMIT :limit", nativeQuery = true)
    List<Product> findTopRatedProducts(@Param("limit") int limit);
    
    /**
     * 查询浏览最多的商品
     */
    @Query(value = "SELECT * FROM product WHERE status = 1 ORDER BY sales DESC LIMIT :limit", nativeQuery = true)
    List<Product> findMostViewedProducts(@Param("limit") int limit);
    
    /**
     * 查询收藏最多的商品
     */
    @Query(value = "SELECT * FROM product WHERE status = 1 ORDER BY sales DESC LIMIT :limit", nativeQuery = true)
    List<Product> findMostFavoritedProducts(@Param("limit") int limit);
    
    /**
     * 按分类统计销售额
     */
    @Query(value = "SELECT c.name as category, COALESCE(SUM(p.sales * p.price), 0) as total_sales " +
                   "FROM category c LEFT JOIN product p ON c.id = p.category_id " +
                   "WHERE p.status = 1 OR p.status IS NULL " +
                   "GROUP BY c.id, c.name " +
                   "ORDER BY total_sales DESC", nativeQuery = true)
    List<Map<String, Object>> findCategorySalesStats();
    
    /**
     * 查询所有商品（用于不分页的列表查询）
     */
    @Query(value = "SELECT * FROM product ORDER BY create_time DESC", nativeQuery = true)
    List<Product> findAllOrderByNameAsc();
}