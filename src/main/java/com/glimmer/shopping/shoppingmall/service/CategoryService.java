package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.Category;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;
import java.util.Map;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 添加分类
     */
    Result<String> addCategory(Category category);

    /**
     * 更新分类
     */
    Result<String> updateCategory(Category category);

    /**
     * 删除分类
     */
    Result<String> deleteCategory(String id);

    /**
     * 批量删除分类
     */
    Result<Integer> batchDeleteCategories(List<String> ids);

    /**
     * 根据ID获取分类详情
     */
    Result<Category> getCategoryById(String id);

    /**
     * 分页查询分类列表
     */
    Result<PageResult<Category>> getCategoryList(String name, Integer level, String parentId, Integer status, Integer page, Integer size);

    /**
     * 获取所有启用的分类列表（树形结构）
     */
    Result<List<Category>> getAllCategoriesTree();

    /**
     * 获取顶级分类列表
     */
    Result<List<Category>> getTopLevelCategories();

    /**
     * 根据父分类ID获取子分类列表
     */
    Result<List<Category>> getChildCategories(String parentId);

    /**
     * 获取所有分类名称（下拉框使用）
     */
    Result<List<String>> getAllCategoryNames();

    /**
     * 获取顶级分类名称（下拉框使用）
     */
    Result<List<String>> getTopLevelCategoryNames();

    /**
     * 根据父分类ID获取子分类名称
     */
    Result<List<String>> getChildCategoryNames(String parentId);

    /**
     * 获取热门分类
     */
    Result<List<Category>> getHotCategories();

    /**
     * 批量导入分类
     */
    Result<Integer> batchImportCategories(List<Category> categories);
}