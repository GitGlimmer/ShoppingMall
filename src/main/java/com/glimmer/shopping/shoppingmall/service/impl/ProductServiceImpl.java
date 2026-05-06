package com.glimmer.shopping.shoppingmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.glimmer.shopping.shoppingmall.dto.ProductDTO;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.repository.ProductRepository;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;

    public ProductServiceImpl(ProductRepository productRepository, StringRedisTemplate redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addProduct(Product product) {
        try {
            if (product == null) {
                return Result.error("商品信息不能为空");
            }
            
            Product savedProduct = productRepository.save(product);
            
            String cacheKey = "product:" + savedProduct.getId();
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(savedProduct), Duration.ofHours(1));
            
            return Result.success(savedProduct.getId());
        } catch (Exception e) {
            log.error("添加商品失败，商品信息: {}", product, e);
            return Result.error("添加商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateProduct(Product product) {
        try {
            if (product == null) {
                return Result.error("商品信息不能为空");
            }
            if (product.getId() == null || product.getId().trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            Product existingProduct = productRepository.findById(product.getId()).orElse(null);
            if (existingProduct == null) {
                return Result.error("商品不存在");
            }
            
            if (product.getName() != null) {
                existingProduct.setName(product.getName());
            }
            if (product.getDescription() != null) {
                existingProduct.setDescription(product.getDescription());
            }
            if (product.getPrice() != null) {
                existingProduct.setPrice(product.getPrice());
            }
            if (product.getStock() != null) {
                existingProduct.setStock(product.getStock());
            }
            if (product.getCategoryId() != null) {
                existingProduct.setCategoryId(product.getCategoryId());
            }
            if (product.getStatus() != null) {
                existingProduct.setStatus(product.getStatus());
            }
            if (product.getImageUrl() != null) {
                existingProduct.setImageUrl(product.getImageUrl());
            }
            if (product.getWeight() != null) {
                existingProduct.setWeight(product.getWeight());
            }
            if (product.getBrand() != null) {
                existingProduct.setBrand(product.getBrand());
            }
            if (product.getSpecifications() != null) {
                existingProduct.setSpecifications(product.getSpecifications());
            }
            if (product.getOrigin() != null) {
                existingProduct.setOrigin(product.getOrigin());
            }
            if (product.getUnit() != null) {
                existingProduct.setUnit(product.getUnit());
            }
            if (product.getTags() != null) {
                existingProduct.setTags(product.getTags());
            }
            
            existingProduct.setUpdateTime(LocalDateTime.now());
            productRepository.save(existingProduct);
            
            String cacheKey = "product:" + product.getId();
            redisTemplate.delete(cacheKey);
            
            return Result.success(product.getId());
        } catch (Exception e) {
            log.error("更新商品失败，商品信息: {}", product, e);
            return Result.error("更新商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteProduct(ProductQueryRequest request) {
        try {
            if (request == null || request.getId() == null || request.getId().trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            String id = request.getId();
            if (!productRepository.existsById(id)) {
                return Result.error("商品不存在");
            }
            
            productRepository.deleteById(id);
            
            String cacheKey = "product:" + id;
            redisTemplate.delete(cacheKey);
            
            return Result.success(id);
        } catch (Exception e) {
            log.error("删除商品失败，请求参数: {}", request, e);
            return Result.error("删除商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateProductStatus(ProductQueryRequest request) {
        try {
            if (request == null || request.getId() == null || request.getId().trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            String id = request.getId();
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            Integer status = request.getStatus() != null && !request.getStatus().isEmpty() 
                    ? Integer.parseInt(request.getStatus()) : null;
            if (status == null) {
                return Result.error("商品状态不能为空");
            }
            
            product.setStatus(status);
            product.setUpdateTime(LocalDateTime.now());
            productRepository.save(product);
            
            String cacheKey = "product:" + id;
            redisTemplate.delete(cacheKey);
            
            return Result.success(id);
        } catch (Exception e) {
            log.error("更新商品状态失败，请求参数: {}", request, e);
            return Result.error("更新商品状态失败");
        }
    }

    @Override
    public Result<ProductDTO> getProductDetail(ProductQueryRequest request) {
        try {
            if (request == null || request.getId() == null || request.getId().trim().isEmpty()) {
                return Result.error("商品ID不能为空");
            }
            
            String id = request.getId();
            String cacheKey = "product:" + id;
            String cachedProduct = redisTemplate.opsForValue().get(cacheKey);
            
            Product product;
            if (cachedProduct != null) {
                product = JSON.parseObject(cachedProduct, Product.class);
            } else {
                product = productRepository.findById(id).orElse(null);
                if (product != null) {
                    redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(product), Duration.ofHours(1));
                }
            }
            
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            return Result.success(ProductDTO.fromEntity(product));
        } catch (Exception e) {
            log.error("获取商品详情失败，请求参数: {}", request, e);
            return Result.error("获取商品详情失败");
        }
    }

    @Override
    public Result<PageResult<ProductDTO>> getProductsByPage(ProductQueryRequest request) {
        try {
            if (request == null) {
                request = new ProductQueryRequest();
            }
            
            int page = request.getPage() != null ? request.getPage() : 1;
            int size = request.getSize() != null ? request.getSize() : 10;
            
            String keyword = request.getKeyword();
            String categoryId = request.getCategoryId();
            String brandId = request.getBrandId();
            Integer statusCode = null;
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                statusCode = Integer.parseInt(request.getStatus());
            }
            
            String startDateStr = request.getStartDate();
            String endDateStr = request.getEndDate();
            
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDateTime.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDateTime.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            
            String sortBy = request.getSortBy();
            String sortDirection = request.getSortDirection();
            
            String defaultSortBy = "name";
            String defaultSortDirection = "ASC";
            
            if (sortBy == null || sortBy.isEmpty()) {
                sortBy = defaultSortBy;
                sortDirection = defaultSortDirection;
            }
            
            List<Product> productList;
            long totalElements;
            
            if ("brandName".equals(sortBy)) {
                int offset = (page - 1) * size;
                if ("ASC".equalsIgnoreCase(sortDirection)) {
                    productList = productRepository.findByConditionsOrderByBrandNameAsc(keyword, categoryId, brandId, statusCode, startDate, endDate, offset, size);
                } else {
                    productList = productRepository.findByConditionsOrderByBrandNameDesc(keyword, categoryId, brandId, statusCode, startDate, endDate, offset, size);
                }
                totalElements = productRepository.countByConditionsForSort(keyword, categoryId, brandId, statusCode, startDate, endDate);
            } else if ("name".equals(sortBy)) {
                int offset = (page - 1) * size;
                if ("ASC".equalsIgnoreCase(sortDirection)) {
                    productList = productRepository.findByConditionsOrderByNameAsc(keyword, categoryId, brandId, statusCode, startDate, endDate, offset, size);
                } else {
                    productList = productRepository.findByConditionsOrderByNameDesc(keyword, categoryId, brandId, statusCode, startDate, endDate, offset, size);
                }
                totalElements = productRepository.countByConditionsForSort(keyword, categoryId, brandId, statusCode, startDate, endDate);
            } else {
                Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
                Page<Product> productPage = productRepository.findByConditions(
                        keyword, categoryId, brandId, statusCode, startDate, endDate, PageRequest.of(page - 1, size, Sort.by(direction, sortBy)));
                productList = productPage.getContent();
                totalElements = productPage.getTotalElements();
            }
            
            List<ProductDTO> dtoList = productList.stream()
                    .map(ProductDTO::fromEntity)
                    .collect(Collectors.toList());
            
            PageResult<ProductDTO> pageResult = new PageResult<>(
                    dtoList,
                    totalElements,
                    page,
                    size
            );
            
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询商品失败，请求参数: {}", request, e);
            return Result.error("分页查询商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchUpdateProductStatus(List<String> ids, Object status) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("商品ID列表不能为空");
            }
            if (status == null) {
                return Result.error("商品状态不能为空");
            }
            
            int count = 0;
            Integer statusCode;
            if (status instanceof Integer) {
                statusCode = (Integer) status;
            } else {
                statusCode = Integer.parseInt(status.toString());
            }
            
            for (String id : ids) {
                Product product = productRepository.findById(id).orElse(null);
                if (product != null) {
                    product.setStatus(statusCode);
                    product.setUpdateTime(LocalDateTime.now());
                    productRepository.save(product);
                    
                    String cacheKey = "product:" + id;
                    redisTemplate.delete(cacheKey);
                    count++;
                }
            }
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量更新商品状态失败，商品ID列表: {}, 状态: {}", ids, status, e);
            return Result.error("批量更新商品状态失败");
        }
    }
}
