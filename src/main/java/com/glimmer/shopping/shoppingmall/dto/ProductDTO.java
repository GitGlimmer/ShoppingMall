package com.glimmer.shopping.shoppingmall.dto;

import com.glimmer.shopping.shoppingmall.entity.Brand;
import com.glimmer.shopping.shoppingmall.entity.Category;
import com.glimmer.shopping.shoppingmall.entity.Product;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDTO {
    
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String categoryId;
    private String categoryName;
    private String categoryPath;
    private String brandId;
    private String brandName;
    private Integer status;
    private String statusDescription;
    private Integer sales;
    private String imageUrl;
    private Integer weight;
    private String specifications;
    private String origin;
    private String unit;
    private String tags;
    private Integer isRecommended;
    private Integer isNew;
    private Integer isHot;
    private Integer sortWeight;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategoryId(product.getCategoryId());
        dto.setBrandId(product.getBrandId());
        dto.setStatus(product.getStatus());
        dto.setStatusDescription(product.getStatusDescription());
        dto.setSales(product.getSales());
        dto.setImageUrl(product.getImageUrl());
        dto.setWeight(product.getWeight());
        dto.setSpecifications(product.getSpecifications());
        dto.setOrigin(product.getOrigin());
        dto.setUnit(product.getUnit());
        dto.setTags(product.getTags());
        dto.setIsRecommended(product.getIsRecommended());
        dto.setIsNew(product.getIsNew());
        dto.setIsHot(product.getIsHot());
        dto.setSortWeight(product.getSortWeight());
        dto.setCreateTime(product.getCreateTime());
        dto.setUpdateTime(product.getUpdateTime());
        
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
            dto.setCategoryPath(buildCategoryPath(product.getCategory()));
        }
        
        if (product.getBrand() != null) {
            dto.setBrandName(product.getBrand().getName());
        }
        
        return dto;
    }
    
    private static String buildCategoryPath(Category category) {
        if (category == null) {
            return "";
        }
        StringBuilder path = new StringBuilder(category.getName());
        Category parent = category.getParent();
        while (parent != null) {
            path.insert(0, parent.getName() + " > ");
            parent = parent.getParent();
        }
        return path.toString();
    }
}