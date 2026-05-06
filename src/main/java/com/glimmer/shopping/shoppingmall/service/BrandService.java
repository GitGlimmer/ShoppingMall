package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.Brand;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;
import java.util.Map;

/**
 * 品牌服务接口
 */
public interface BrandService {

    /**
     * 添加品牌
     */
    Result<String> addBrand(Brand brand);

    /**
     * 更新品牌
     */
    Result<String> updateBrand(Brand brand);

    /**
     * 删除品牌
     */
    Result<String> deleteBrand(String id);

    /**
     * 批量删除品牌
     */
    Result<Integer> batchDeleteBrands(List<String> ids);

    /**
     * 根据ID获取品牌详情
     */
    Result<Brand> getBrandById(String id);

    /**
     * 分页查询品牌列表
     */
    Result<PageResult<Brand>> getBrandList(String name, String category, Integer status, Integer page, Integer size);

    /**
     * 获取所有启用的品牌列表
     */
    Result<List<Brand>> getAllEnabledBrands();

    /**
     * 根据分类获取品牌列表
     */
    Result<List<Brand>> getBrandsByCategory(String category);

    /**
     * 获取所有品牌名称（下拉框使用）
     */
    Result<List<String>> getAllBrandNames();

    /**
     * 根据分类获取品牌名称（下拉框使用）
     */
    Result<List<String>> getBrandNamesByCategory(String category);

    /**
     * 获取所有品牌分类
     */
    Result<List<String>> getAllCategories();

    /**
     * 批量导入品牌
     */
    Result<Integer> batchImportBrands(List<Brand> brands);
}