package com.glimmer.shopping.shoppingmall.service.impl;

import com.glimmer.shopping.shoppingmall.entity.Brand;
import com.glimmer.shopping.shoppingmall.repository.BrandRepository;
import com.glimmer.shopping.shoppingmall.service.BrandService;
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
 * 品牌服务实现类
 */
@Service
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addBrand(Brand brand) {
        try {
            if (brand == null || brand.getName() == null || brand.getName().trim().isEmpty()) {
                return Result.error("品牌名称不能为空");
            }

            if (brandRepository.existsByName(brand.getName().trim())) {
                return Result.error("品牌名称已存在");
            }

            brand.setId(UUID.randomUUID().toString().replace("-", ""));
            brand.setName(brand.getName().trim());
            brandRepository.save(brand);

            return Result.success("添加成功");
        } catch (Exception e) {
            log.error("添加品牌失败", e);
            return Result.error("添加品牌失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateBrand(Brand brand) {
        try {
            if (brand == null || brand.getId() == null || brand.getId().trim().isEmpty()) {
                return Result.error("品牌ID不能为空");
            }

            Brand existingBrand = brandRepository.findById(brand.getId()).orElse(null);
            if (existingBrand == null) {
                return Result.error("品牌不存在");
            }

            if (brand.getName() != null && !brand.getName().trim().isEmpty()) {
                Brand sameNameBrand = brandRepository.findByName(brand.getName().trim()).orElse(null);
                if (sameNameBrand != null && !sameNameBrand.getId().equals(brand.getId())) {
                    return Result.error("品牌名称已存在");
                }
                existingBrand.setName(brand.getName().trim());
            }

            if (brand.getLogo() != null) {
                existingBrand.setLogo(brand.getLogo());
            }
            if (brand.getDescription() != null) {
                existingBrand.setDescription(brand.getDescription());
            }
            if (brand.getCountry() != null) {
                existingBrand.setCountry(brand.getCountry());
            }
            if (brand.getCategory() != null) {
                existingBrand.setCategory(brand.getCategory());
            }
            if (brand.getStatus() != null) {
                existingBrand.setStatus(brand.getStatus());
            }
            if (brand.getSortOrder() != null) {
                existingBrand.setSortOrder(brand.getSortOrder());
            }

            brandRepository.save(existingBrand);

            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新品牌失败", e);
            return Result.error("更新品牌失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteBrand(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("品牌ID不能为空");
            }

            if (!brandRepository.existsById(id)) {
                return Result.error("品牌不存在");
            }

            brandRepository.deleteById(id);

            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除品牌失败", e);
            return Result.error("删除品牌失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchDeleteBrands(List<String> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的品牌");
            }

            int deletedCount = 0;
            for (String id : ids) {
                if (brandRepository.existsById(id)) {
                    brandRepository.deleteById(id);
                    deletedCount++;
                }
            }

            return Result.success(deletedCount);
        } catch (Exception e) {
            log.error("批量删除品牌失败", e);
            return Result.error("批量删除品牌失败");
        }
    }

    @Override
    public Result<Brand> getBrandById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("品牌ID不能为空");
            }

            Brand brand = brandRepository.findById(id).orElse(null);
            if (brand == null) {
                return Result.error("品牌不存在");
            }

            return Result.success(brand);
        } catch (Exception e) {
            log.error("获取品牌详情失败", e);
            return Result.error("获取品牌详情失败");
        }
    }

    @Override
    public Result<PageResult<Brand>> getBrandList(String name, String category, Integer status, Integer page, Integer size) {
        try {
            int pageNum = page != null ? page : 1;
            int pageSize = size != null ? size : 10;

            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "sortOrder", "name"));

            Page<Brand> brandPage;

            if (name != null && !name.isEmpty()) {
                brandPage = brandRepository.findAll((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("name"), "%" + name + "%"), pageable);
            } else if (category != null && !category.isEmpty()) {
                brandPage = brandRepository.findAll((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("category"), category), pageable);
            } else if (status != null) {
                brandPage = brandRepository.findAll((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("status"), status), pageable);
            } else {
                brandPage = brandRepository.findAll(pageable);
            }

            return Result.success(PageResult.of(brandPage.getContent(), brandPage.getTotalElements(), pageNum, pageSize).sortByName(Brand::getName));
        } catch (Exception e) {
            log.error("查询品牌列表失败", e);
            return Result.error("查询品牌列表失败");
        }
    }

    @Override
    public Result<List<Brand>> getAllEnabledBrands() {
        try {
            List<Brand> brands = brandRepository.findByStatusOrderByNameAsc(1);
            return Result.success(brands);
        } catch (Exception e) {
            log.error("获取品牌列表失败", e);
            return Result.error("获取品牌列表失败");
        }
    }

    @Override
    public Result<List<Brand>> getBrandsByCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return getAllEnabledBrands();
            }

            List<Brand> brands = brandRepository.findByCategoryAndStatusOrderBySortOrderAscNameAsc(category.trim(), 1);
            return Result.success(brands);
        } catch (Exception e) {
            log.error("根据分类获取品牌列表失败", e);
            return Result.error("根据分类获取品牌列表失败");
        }
    }

    @Override
    public Result<List<String>> getAllBrandNames() {
        try {
            List<String> brandNames = brandRepository.findAllEnabledBrandNames();
            return Result.success(brandNames);
        } catch (Exception e) {
            log.error("获取品牌名称列表失败", e);
            return Result.error("获取品牌名称列表失败");
        }
    }

    @Override
    public Result<List<String>> getBrandNamesByCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return getAllBrandNames();
            }

            List<String> brandNames = brandRepository.findEnabledBrandNamesByCategory(category.trim());
            return Result.success(brandNames);
        } catch (Exception e) {
            log.error("根据分类获取品牌名称失败", e);
            return Result.error("根据分类获取品牌名称失败");
        }
    }

    @Override
    public Result<List<String>> getAllCategories() {
        try {
            List<String> categories = brandRepository.findAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取品牌分类列表失败", e);
            return Result.error("获取品牌分类列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchImportBrands(List<Brand> brands) {
        try {
            if (brands == null || brands.isEmpty()) {
                return Result.error("请提供要导入的品牌数据");
            }

            int importedCount = 0;
            for (Brand brand : brands) {
                if (brand.getName() == null || brand.getName().trim().isEmpty()) {
                    continue;
                }

                if (brandRepository.existsByName(brand.getName().trim())) {
                    continue;
                }

                brand.setId(UUID.randomUUID().toString().replace("-", ""));
                brand.setName(brand.getName().trim());
                if (brand.getStatus() == null) {
                    brand.setStatus(1);
                }
                if (brand.getSortOrder() == null) {
                    brand.setSortOrder(0);
                }

                brandRepository.save(brand);
                importedCount++;
            }

            return Result.success(importedCount);
        } catch (Exception e) {
            log.error("批量导入品牌失败", e);
            return Result.error("批量导入品牌失败");
        }
    }
}