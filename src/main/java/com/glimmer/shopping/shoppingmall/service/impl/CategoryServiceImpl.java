package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.entity.Category;
import com.glimmer.shopping.shoppingmall.repository.CategoryRepository;
import com.glimmer.shopping.shoppingmall.service.CategoryService;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 分类服务实现类
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addCategory(Category category) {
        try {
            if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
                return Result.error("分类名称不能为空");
            }

            if (category.getLevel() == null) {
                category.setLevel(1);
            }

            if (category.getLevel() > 1 && (category.getParentId() == null || category.getParentId().trim().isEmpty())) {
                return Result.error("非顶级分类必须指定父分类");
            }

            if (category.getLevel() > 1) {
                Category parent = categoryRepository.findById(category.getParentId()).orElse(null);
                if (parent == null) {
                    return Result.error("父分类不存在");
                }
                if (parent.getLevel() != category.getLevel() - 1) {
                    return Result.error("分类层级不正确");
                }
            }

            if (categoryRepository.existsByNameAndLevel(category.getName().trim(), category.getLevel())) {
                return Result.error("同层级下分类名称已存在");
            }

            category.setId(UUID.randomUUID().toString().replace("-", ""));
            category.setName(category.getName().trim());
            categoryRepository.save(category);

            return Result.success("添加成功");
        } catch (Exception e) {
            log.error("添加分类失败", e);
            return Result.error("添加分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateCategory(Category category) {
        try {
            if (category == null || category.getId() == null || category.getId().trim().isEmpty()) {
                return Result.error("分类ID不能为空");
            }

            Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
            if (existingCategory == null) {
                return Result.error("分类不存在");
            }

            if (category.getName() != null && !category.getName().trim().isEmpty()) {
                if (categoryRepository.existsByNameAndLevelAndIdNot(category.getName().trim(), existingCategory.getLevel(), category.getId())) {
                    return Result.error("同层级下分类名称已存在");
                }
                existingCategory.setName(category.getName().trim());
            }

            if (category.getParentId() != null) {
                if (category.getParentId().isEmpty()) {
                    existingCategory.setParentId(null);
                    existingCategory.setLevel(1);
                } else {
                    Category parent = categoryRepository.findById(category.getParentId()).orElse(null);
                    if (parent == null) {
                        return Result.error("父分类不存在");
                    }
                    existingCategory.setParentId(category.getParentId());
                    existingCategory.setLevel(parent.getLevel() + 1);
                }
            }

            if (category.getIcon() != null) {
                existingCategory.setIcon(category.getIcon());
            }
            if (category.getDescription() != null) {
                existingCategory.setDescription(category.getDescription());
            }
            if (category.getStatus() != null) {
                existingCategory.setStatus(category.getStatus());
            }
            if (category.getSortOrder() != null) {
                existingCategory.setSortOrder(category.getSortOrder());
            }
            if (category.getIsHot() != null) {
                existingCategory.setIsHot(category.getIsHot());
            }

            categoryRepository.save(existingCategory);

            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.error("更新分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteCategory(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("分类ID不能为空");
            }

            if (!categoryRepository.existsById(id)) {
                return Result.error("分类不存在");
            }

            List<Category> children = categoryRepository.findByParentIdAndStatusOrderBySortOrderAsc(id, 1);
            if (!children.isEmpty()) {
                return Result.error("该分类下存在子分类，无法删除");
            }

            categoryRepository.deleteById(id);

            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.error("删除分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchDeleteCategories(List<String> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的分类");
            }

            int deletedCount = 0;
            for (String id : ids) {
                List<Category> children = categoryRepository.findByParentIdAndStatusOrderBySortOrderAsc(id, 1);
                if (children.isEmpty() && categoryRepository.existsById(id)) {
                    categoryRepository.deleteById(id);
                    deletedCount++;
                }
            }

            return Result.success(deletedCount);
        } catch (Exception e) {
            log.error("批量删除分类失败", e);
            return Result.error("批量删除分类失败");
        }
    }

    @Override
    public Result<Category> getCategoryById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("分类ID不能为空");
            }

            Category category = categoryRepository.findById(id).orElse(null);
            if (category == null) {
                return Result.error("分类不存在");
            }

            return Result.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return Result.error("获取分类详情失败");
        }
    }

    @Override
    public Result<PageResult<Category>> getCategoryList(String name, Integer level, String parentId, Integer status, Integer page, Integer size) {
        try {
            int pageNum = page != null ? page : 1;
            int pageSize = size != null ? size : 10;

            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "level", "sortOrder"));

            Page<Category> categoryPage;

            if (name != null && !name.isEmpty()) {
                categoryPage = categoryRepository.findAll((root, query, cb) ->
                        cb.like(root.get("name"), "%" + name + "%"), pageable);
            } else if (level != null) {
                categoryPage = categoryRepository.findAll((root, query, cb) ->
                        cb.equal(root.get("level"), level), pageable);
            } else if (parentId != null && !parentId.isEmpty()) {
                categoryPage = categoryRepository.findAll((root, query, cb) ->
                        cb.equal(root.get("parentId"), parentId), pageable);
            } else if (status != null) {
                categoryPage = categoryRepository.findAll((root, query, cb) ->
                        cb.equal(root.get("status"), status), pageable);
            } else {
                categoryPage = categoryRepository.findAll(pageable);
            }

            return Result.success(PageResult.of(categoryPage.getContent(), categoryPage.getTotalElements(), pageNum, pageSize));
        } catch (Exception e) {
            log.error("查询分类列表失败", e);
            return Result.error("查询分类列表失败");
        }
    }

    @Override
    public Result<List<Category>> getAllCategoriesTree() {
        try {
            List<Category> topLevelCategories = categoryRepository.findByLevelAndStatusOrderBySortOrderAsc(1, 1);
            
            for (Category top : topLevelCategories) {
                List<Category> secondLevel = categoryRepository.findByParentIdAndStatusOrderBySortOrderAsc(top.getId(), 1);
                top.setChildren(secondLevel);
                
                for (Category second : secondLevel) {
                    List<Category> thirdLevel = categoryRepository.findByParentIdAndStatusOrderBySortOrderAsc(second.getId(), 1);
                    second.setChildren(thirdLevel);
                }
            }

            return Result.success(topLevelCategories);
        } catch (Exception e) {
            log.error("获取分类树形结构失败", e);
            return Result.error("获取分类树形结构失败");
        }
    }

    @Override
    public Result<List<Category>> getTopLevelCategories() {
        try {
            List<Category> categories = categoryRepository.findByLevelAndStatusOrderBySortOrderAsc(1, 1);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取顶级分类失败", e);
            return Result.error("获取顶级分类失败");
        }
    }

    @Override
    public Result<List<Category>> getChildCategories(String parentId) {
        try {
            if (parentId == null || parentId.trim().isEmpty()) {
                return getTopLevelCategories();
            }

            List<Category> categories = categoryRepository.findByParentIdAndStatusOrderBySortOrderAsc(parentId.trim(), 1);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取子分类失败", e);
            return Result.error("获取子分类失败");
        }
    }

    @Override
    public Result<List<String>> getAllCategoryNames() {
        try {
            List<String> categoryNames = categoryRepository.findAllEnabledCategoryNames();
            return Result.success(categoryNames);
        } catch (Exception e) {
            log.error("获取分类名称列表失败", e);
            return Result.error("获取分类名称列表失败");
        }
    }

    @Override
    public Result<List<String>> getTopLevelCategoryNames() {
        try {
            List<String> categoryNames = categoryRepository.findTopLevelCategoryNames();
            return Result.success(categoryNames);
        } catch (Exception e) {
            log.error("获取顶级分类名称失败", e);
            return Result.error("获取顶级分类名称失败");
        }
    }

    @Override
    public Result<List<String>> getChildCategoryNames(String parentId) {
        try {
            if (parentId == null || parentId.trim().isEmpty()) {
                return getTopLevelCategoryNames();
            }

            List<String> categoryNames = categoryRepository.findChildCategoryNamesByParentId(parentId.trim());
            return Result.success(categoryNames);
        } catch (Exception e) {
            log.error("获取子分类名称失败", e);
            return Result.error("获取子分类名称失败");
        }
    }

    @Override
    public Result<List<Category>> getHotCategories() {
        try {
            List<Category> categories = categoryRepository.findByIsHotAndStatusOrderBySortOrderAsc(1, 1);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取热门分类失败", e);
            return Result.error("获取热门分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchImportCategories(List<Category> categories) {
        try {
            if (categories == null || categories.isEmpty()) {
                return Result.error("请提供要导入的分类数据");
            }

            int importedCount = 0;
            for (Category category : categories) {
                if (category.getName() == null || category.getName().trim().isEmpty()) {
                    continue;
                }

                if (category.getLevel() == null) {
                    category.setLevel(1);
                }

                if (categoryRepository.existsByNameAndLevel(category.getName().trim(), category.getLevel())) {
                    continue;
                }

                category.setId(UUID.randomUUID().toString().replace("-", ""));
                category.setName(category.getName().trim());
                if (category.getStatus() == null) {
                    category.setStatus(1);
                }
                if (category.getSortOrder() == null) {
                    category.setSortOrder(0);
                }
                if (category.getIsHot() == null) {
                    category.setIsHot(0);
                }

                categoryRepository.save(category);
                importedCount++;
            }

            return Result.success(importedCount);
        } catch (Exception e) {
            log.error("批量导入分类失败", e);
            return Result.error("批量导入分类失败");
        }
    }
}