package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Glimmer
 */
@Entity
@Table(name = "product")
@Data
public class Product {
    /** id */
    @Id
    private String id;
    
    /** 商品名称 */
    private String name;
    
    /** 商品描述 */
    private String description;
    
    /** 商品价格 */
    private String price;
    
    /** 商品库存 */
    private String stock;
    
    /** 商品分类 */
    private String category;
    
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 商品状态：ON_SALE-上架，OFF_SALE-下架 */
    private String status;

    /** 商品销量 */
    private Integer sales;

    /** 商品图片URL */
    private String imageUrl;
    
    /** 商品重量（克） */
    private Integer weight;
    
    /** 商品品牌 */
    private String brand;
    
    /** 商品规格 */
    private String specifications;
    
    /** 商品产地 */
    private String origin;
    
    /** 商品单位（件、个、套等） */
    private String unit;
    
    /** 商品标签（多个标签用逗号分隔） */
    private String tags;
    
    /** 商品是否推荐 */
    private Boolean isRecommended;
    
    /** 商品是否新品 */
    private Boolean isNew;
    
    /** 商品是否热销 */
    private Boolean isHot;
    
    /** 商品排序权重 */
    private Integer sortWeight;
    
    /**
     * 保存前自动生成UUID
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUIDUtil.generateUUID();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        this.updateTime = LocalDateTime.now();
        
        // 设置默认值
        if (this.status == null) {
            this.status = "ON_SALE"; // 默认上架
        }
        if (this.sales == null) {
            this.sales = 0; // 默认销量为0
        }
        if (this.isRecommended == null) {
            this.isRecommended = false; // 默认不推荐
        }
        if (this.isNew == null) {
            this.isNew = false; // 默认不是新品
        }
        if (this.isHot == null) {
            this.isHot = false; // 默认不是热销
        }
        if (this.sortWeight == null) {
            this.sortWeight = 0; // 默认排序权重
        }
    }
}