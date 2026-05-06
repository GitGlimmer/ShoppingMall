package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 品牌Repository接口
 */
public interface BrandRepository extends JpaRepository<Brand, String>, JpaSpecificationExecutor<Brand> {

    /**
     * 根据品牌名称查询
     */
    Optional<Brand> findByName(String name);

    /**
     * 查询启用状态的品牌列表（按名称首字母排序，支持中英文，纯数字排最后）
     */
    @Query(value = "SELECT * FROM brand WHERE status = ?1 ORDER BY " +
                   "CASE WHEN name REGEXP '^[0-9]+$' THEN 2 ELSE 1 END, " +
                   "CONVERT(name USING gbk) ASC", nativeQuery = true)
    List<Brand> findByStatusOrderByNameAsc(Integer status);

    /**
     * 根据分类查询品牌列表（启用状态）
     */
    List<Brand> findByCategoryAndStatusOrderBySortOrderAscNameAsc(String category, Integer status);

    /**
     * 根据名称模糊查询品牌
     */
    List<Brand> findByNameContainingAndStatusOrderByNameAsc(String name, Integer status);

    /**
     * 查询所有品牌名称（启用状态）
     */
    @Query("SELECT b.name FROM Brand b WHERE b.status = 1 ORDER BY b.sortOrder ASC, b.name ASC")
    List<String> findAllEnabledBrandNames();

    /**
     * 根据分类查询品牌名称（启用状态）
     */
    @Query("SELECT b.name FROM Brand b WHERE b.status = 1 AND b.category = :category ORDER BY b.sortOrder ASC, b.name ASC")
    List<String> findEnabledBrandNamesByCategory(@Param("category") String category);

    /**
     * 检查品牌名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 查询所有分类（去重）
     */
    @Query("SELECT DISTINCT b.category FROM Brand b WHERE b.status = 1 ORDER BY b.category")
    List<String> findAllCategories();
}