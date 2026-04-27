package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Glimmer
 */
public interface ProductRepository extends JpaRepository<Product, String> {
    
    /**
     * 按分类查询商品
     */
    List<Product> findByCategory(String category);
    
    /**
     * 按状态查询商品
     */
    List<Product> findByStatus(String status);
    
    /**
     * 按商品名称模糊查询
     */
    List<Product> findByNameContaining(String name);
    
    /**
     * 按价格范围查询商品
     */
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") String minPrice, @Param("maxPrice") String maxPrice);
    
    /**
     * 按销量降序查询商品
     */
    List<Product> findByOrderBySalesDesc();
    
    /**
     * 查询热门商品（按销量排序，限制数量）
     */
    @Query(value = "SELECT * FROM product WHERE status = 'ON_SALE' ORDER BY sales DESC LIMIT :limit", nativeQuery = true)
    List<Product> findHotProducts(@Param("limit") int limit);
    
    /**
     * 统计商品总数
     */
    @Query("SELECT COUNT(p) FROM Product p")
    Long countAllProducts();
    
    /**
     * 统计上架商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'ON_SALE'")
    Long countOnSaleProducts();
    
    /**
     * 统计下架商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'OFF_SALE'")
    Long countOffSaleProducts();
    
    /**
     * 按品牌查询商品
     */
    List<Product> findByBrand(String brand);
    
    /**
     * 按是否推荐查询商品
     */
    List<Product> findByIsRecommendedTrue();
    
    /**
     * 按是否新品查询商品
     */
    List<Product> findByIsNewTrue();
    
    /**
     * 按是否热销查询商品
     */
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
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isRecommended = true")
    Long countRecommendedProducts();
    
    /**
     * 统计新品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isNew = true")
    Long countNewProducts();
    
    /**
     * 统计热销商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isHot = true")
    Long countHotProducts();
}