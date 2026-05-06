package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.entity.Category;
import com.glimmer.shopping.shoppingmall.service.CategoryService;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类管理控制器
 */
@RestController
@RequestMapping("/api/category")
@Api(tags = "分类管理")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ApiOperation("新增分类")
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @ApiOperation("更新分类")
    @PutMapping
    public Result<String> updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable String id) {
        return categoryService.deleteCategory(id);
    }

    @ApiOperation("批量删除分类")
    @PostMapping("/batch/delete")
    public Result<Integer> batchDeleteCategories(@RequestBody Map<String, List<String>> request) {
        List<String> ids = request.get("ids");
        return categoryService.batchDeleteCategories(ids);
    }

    @ApiOperation("获取分类详情")
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    @ApiOperation("分页查询分类列表")
    @PostMapping("/list")
    public Result<PageResult<Category>> getCategoryList(@RequestBody(required = false) Map<String, Object> request) {
        String name = request != null ? (String) request.get("name") : null;
        Integer level = request != null && request.get("level") != null ? ((Number) request.get("level")).intValue() : null;
        String parentId = request != null ? (String) request.get("parentId") : null;
        Integer status = request != null && request.get("status") != null ? ((Number) request.get("status")).intValue() : null;
        Integer page = request != null && request.get("page") != null ? ((Number) request.get("page")).intValue() : 1;
        Integer size = request != null && request.get("size") != null ? ((Number) request.get("size")).intValue() : 10;

        return categoryService.getCategoryList(name, level, parentId, status, page, size);
    }

    @ApiOperation("获取分类树形结构")
    @GetMapping("/tree")
    public Result<List<Category>> getAllCategoriesTree() {
        return categoryService.getAllCategoriesTree();
    }

    @ApiOperation("获取顶级分类列表")
    @GetMapping("/top")
    public Result<List<Category>> getTopLevelCategories() {
        return categoryService.getTopLevelCategories();
    }

    @ApiOperation("获取子分类列表")
    @GetMapping("/children/{parentId}")
    public Result<List<Category>> getChildCategories(@PathVariable String parentId) {
        return categoryService.getChildCategories(parentId);
    }

    @ApiOperation("获取所有分类名称（下拉框使用）")
    @GetMapping("/names")
    public Result<List<String>> getAllCategoryNames() {
        return categoryService.getAllCategoryNames();
    }

    @ApiOperation("获取顶级分类名称（下拉框使用）")
    @GetMapping("/names/top")
    public Result<List<String>> getTopLevelCategoryNames() {
        return categoryService.getTopLevelCategoryNames();
    }

    @ApiOperation("根据父分类ID获取子分类名称")
    @GetMapping("/names/children/{parentId}")
    public Result<List<String>> getChildCategoryNames(@PathVariable String parentId) {
        return categoryService.getChildCategoryNames(parentId);
    }

    @ApiOperation("获取热门分类")
    @GetMapping("/hot")
    public Result<List<Category>> getHotCategories() {
        return categoryService.getHotCategories();
    }

    @ApiOperation("批量导入分类")
    @PostMapping("/batch/import")
    public Result<Integer> batchImportCategories(@RequestBody List<Category> categories) {
        return categoryService.batchImportCategories(categories);
    }
}