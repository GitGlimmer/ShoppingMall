package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.entity.Brand;
import com.glimmer.shopping.shoppingmall.service.BrandService;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 品牌管理控制器
 */
@RestController
@RequestMapping("/api/brand")
@Api(tags = "品牌管理")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @ApiOperation("新增品牌")
    @PostMapping
    public Result<String> addBrand(@RequestBody Brand brand) {
        return brandService.addBrand(brand);
    }

    @ApiOperation("更新品牌")
    @PutMapping
    public Result<String> updateBrand(@RequestBody Brand brand) {
        return brandService.updateBrand(brand);
    }

    @ApiOperation("删除品牌")
    @DeleteMapping("/{id}")
    public Result<String> deleteBrand(@PathVariable String id) {
        return brandService.deleteBrand(id);
    }

    @ApiOperation("批量删除品牌")
    @PostMapping("/batch/delete")
    public Result<Integer> batchDeleteBrands(@RequestBody Map<String, List<String>> request) {
        List<String> ids = request.get("ids");
        return brandService.batchDeleteBrands(ids);
    }

    @ApiOperation("获取品牌详情")
    @GetMapping("/{id}")
    public Result<Brand> getBrandById(@PathVariable String id) {
        return brandService.getBrandById(id);
    }

    @ApiOperation("分页查询品牌列表")
    @PostMapping("/list")
    public Result<PageResult<Brand>> getBrandList(@RequestBody(required = false) Map<String, Object> request) {
        String name = request != null ? (String) request.get("name") : null;
        String category = request != null ? (String) request.get("category") : null;
        Integer status = request != null && request.get("status") != null ? ((Number) request.get("status")).intValue() : null;
        Integer page = request != null && request.get("page") != null ? ((Number) request.get("page")).intValue() : 1;
        Integer size = request != null && request.get("size") != null ? ((Number) request.get("size")).intValue() : 10;

        return brandService.getBrandList(name, category, status, page, size);
    }

    @ApiOperation("获取所有启用的品牌列表")
    @GetMapping("/all")
    public Result<List<Brand>> getAllEnabledBrands() {
        return brandService.getAllEnabledBrands();
    }

    @ApiOperation("根据分类获取品牌列表")
    @GetMapping("/category/{category}")
    public Result<List<Brand>> getBrandsByCategory(@PathVariable String category) {
        return brandService.getBrandsByCategory(category);
    }

    @ApiOperation("获取所有品牌名称（下拉框使用）")
    @GetMapping("/names")
    public Result<List<String>> getAllBrandNames() {
        return brandService.getAllBrandNames();
    }

    @ApiOperation("根据分类获取品牌名称（下拉框使用）")
    @GetMapping("/names/category/{category}")
    public Result<List<String>> getBrandNamesByCategory(@PathVariable String category) {
        return brandService.getBrandNamesByCategory(category);
    }

    @ApiOperation("获取所有品牌分类")
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        return brandService.getAllCategories();
    }

    @ApiOperation("批量导入品牌")
    @PostMapping("/batch/import")
    public Result<Integer> batchImportBrands(@RequestBody List<Brand> brands) {
        return brandService.batchImportBrands(brands);
    }
}