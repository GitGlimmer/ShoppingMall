package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 分类Repository接口
 */
public interface CategoryRepository extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {

    /**
     * 根据分类名称查询
     */
    Optional<Category> findByName(String name);

    /**
     * 查询顶级分类（level=1）
     */
    List<Category> findByLevelAndStatusOrderBySortOrderAsc(Integer level, Integer status);

    /**
     * 根据父分类ID查询子分类
     */
    List<Category> findByParentIdAndStatusOrderBySortOrderAsc(String parentId, Integer status);

    /**
     * 根据层级查询分类
     */
    List<Category> findByLevelOrderBySortOrderAsc(Integer level);

    /**
     * 查询所有启用状态的分类
     */
    List<Category> findByStatusOrderByLevelAscSortOrderAsc(Integer status);

    /**
     * 查询热门分类
     */
    List<Category> findByIsHotAndStatusOrderBySortOrderAsc(Integer isHot, Integer status);

    /**
     * 根据名称模糊查询
     */
    List<Category> findByNameContainingAndStatusOrderByNameAsc(String name, Integer status);

    /**
     * 查询所有分类名称（启用状态）
     */
    @Query("SELECT c.name FROM Category c WHERE c.status = 1 ORDER BY c.level ASC, c.sortOrder ASC")
    List<String> findAllEnabledCategoryNames();

    /**
     * 查询顶级分类名称（启用状态）
     */
    @Query("SELECT c.name FROM Category c WHERE c.level = 1 AND c.status = 1 ORDER BY c.sortOrder ASC")
    List<String> findTopLevelCategoryNames();

    /**
     * 根据父分类ID查询子分类名称
     */
    @Query("SELECT c.name FROM Category c WHERE c.parentId = :parentId AND c.status = 1 ORDER BY c.sortOrder ASC")
    List<String> findChildCategoryNamesByParentId(@Param("parentId") String parentId);

    /**
     * 检查分类名称是否存在（同层级下）
     */
    boolean existsByNameAndLevel(String name, Integer level);

    /**
     * 检查分类名称是否存在（排除指定ID）
     */
    boolean existsByNameAndLevelAndIdNot(String name, Integer level, String id);
}