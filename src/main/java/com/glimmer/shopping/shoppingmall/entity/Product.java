package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.enums.BooleanStatusEnum;
import com.glimmer.shopping.shoppingmall.enums.ProductStatusEnum;
import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 
 * 存储商品的基本信息，包括名称、价格、库存、分类、状态等
 * 支持商品的上架/下架、推荐、新品、热销等标记
 * 
 * @author Glimmer
 */
@Entity
@Table(name = "product", indexes = {
    @Index(name = "idx_product_category_id", columnList = "category_id"),
    @Index(name = "idx_product_brand_id", columnList = "brand_id"),
    @Index(name = "idx_product_status", columnList = "status"),
    @Index(name = "idx_product_create_time", columnList = "create_time"),
    @Index(name = "idx_product_sales", columnList = "sales")
})
@Data
public class Product {
    
    /** 商品唯一标识ID */
    @Id
    @Column(name = "id", length = 36, nullable = false, columnDefinition = "VARCHAR(36) COMMENT '商品唯一标识ID'")
    private String id;
    
    /** 商品名称 */
    @Column(name = "name", length = 255, nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '商品名称'")
    private String name;
    
    /** 商品详细描述 */
    @Column(name = "description", columnDefinition = "TEXT COMMENT '商品详细描述'")
    private String description;
    
    /** 商品价格（单位：元） */
    @Column(name = "price", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00 COMMENT '商品价格（单位：元）'")
    private Double price;
    
    /** 商品库存数量 */
    @Column(name = "stock", columnDefinition = "INT DEFAULT 0 COMMENT '商品库存数量'")
    private Integer stock;
    
    /** 商品分类ID */
    @Column(name = "category_id", length = 32, columnDefinition = "VARCHAR(32) COMMENT '商品分类ID'")
    private String categoryId;
    
    /** 商品分类（关联关系） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Category category;
    
    /** 商品创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '商品创建时间'")
    private LocalDateTime createTime;
    
    /** 商品更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '商品更新时间'")
    private LocalDateTime updateTime;
    
    /** 商品状态：1-上架(ON_SALE)，0-下架(OFF_SALE) */
    @Column(name = "status", columnDefinition = "INT DEFAULT 1 COMMENT '商品状态：1-上架(ON_SALE)，0-下架(OFF_SALE)'")
    private Integer status;
    
    /** 商品累计销量 */
    @Column(name = "sales", columnDefinition = "INT DEFAULT 0 COMMENT '商品累计销量'")
    private Integer sales;
    
    /** 商品主图URL */
    @Column(name = "image_url", length = 500, columnDefinition = "VARCHAR(500) COMMENT '商品主图URL'")
    private String imageUrl;
    
    /** 商品重量（单位：克） */
    @Column(name = "weight", columnDefinition = "INT COMMENT '商品重量（单位：克）'")
    private Integer weight;
    
    /** 商品品牌ID */
    @Column(name = "brand_id", length = 32, columnDefinition = "VARCHAR(32) COMMENT '商品品牌ID'")
    private String brandId;
    
    /** 商品品牌（关联关系） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Brand brand;
    
    /** 商品规格参数 */
    @Column(name = "specifications", length = 500, columnDefinition = "VARCHAR(500) COMMENT '商品规格参数'")
    private String specifications;
    
    /** 商品产地 */
    @Column(name = "origin", length = 100, columnDefinition = "VARCHAR(100) COMMENT '商品产地'")
    private String origin;
    
    /** 商品单位（件、个、套等） */
    @Column(name = "unit", length = 20, columnDefinition = "VARCHAR(20) COMMENT '商品单位（件、个、套等）'")
    private String unit;
    
    /** 商品标签，多个标签用逗号分隔 */
    @Column(name = "tags", length = 500, columnDefinition = "VARCHAR(500) COMMENT '商品标签，多个标签用逗号分隔'")
    private String tags;
    
    /** 是否推荐商品：1-是，0-否 */
    @Column(name = "is_recommended", columnDefinition = "INT DEFAULT 0 COMMENT '是否推荐商品：1-是，0-否'")
    private Integer isRecommended;
    
    /** 是否新品：1-是，0-否 */
    @Column(name = "is_new", columnDefinition = "INT DEFAULT 0 COMMENT '是否新品：1-是，0-否'")
    private Integer isNew;
    
    /** 是否热销：1-是，0-否 */
    @Column(name = "is_hot", columnDefinition = "INT DEFAULT 0 COMMENT '是否热销：1-是，0-否'")
    private Integer isHot;
    
    /** 排序权重，数值越大排序越靠前 */
    @Column(name = "sort_weight", columnDefinition = "INT DEFAULT 0 COMMENT '排序权重，数值越大排序越靠前'")
    private Integer sortWeight;
    
    /**
     * 保存前自动生成UUID并设置默认值
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
        
        if (this.status == null) {
            this.status = ProductStatusEnum.ON_SALE.getCode();
        }
        if (this.sales == null) {
            this.sales = 0;
        }
        if (this.isRecommended == null) {
            this.isRecommended = BooleanStatusEnum.NO.getCode();
        }
        if (this.isNew == null) {
            this.isNew = BooleanStatusEnum.NO.getCode();
        }
        if (this.isHot == null) {
            this.isHot = BooleanStatusEnum.NO.getCode();
        }
        if (this.sortWeight == null) {
            this.sortWeight = 0;
        }
        if (this.stock == null) {
            this.stock = 0;
        }
        if (this.price == null) {
            this.price = 0.0;
        }
    }
    
    /**
     * 获取状态描述
     * 
     * @return 状态描述
     */
    @Transient
    public String getStatusDescription() {
        return ProductStatusEnum.fromCode(this.status).getDescription();
    }
    
    /**
     * 获取状态字符串
     * 
     * @return 状态字符串
     */
    @Transient
    public String getStatusString() {
        return ProductStatusEnum.fromCode(this.status).getStatus();
    }
    
    /**
     * 判断是否上架
     * 
     * @return true-上架，false-下架
     */
    @Transient
    public boolean isOnSale() {
        return ProductStatusEnum.ON_SALE.getCode().equals(this.status);
    }
    
    /**
     * 判断是否推荐
     * 
     * @return true-推荐，false-不推荐
     */
    @Transient
    public boolean getIsRecommendedBoolean() {
        return BooleanStatusEnum.YES.getCode().equals(this.isRecommended);
    }
    
    /**
     * 判断是否新品
     * 
     * @return true-新品，false-非新品
     */
    @Transient
    public boolean getIsNewBoolean() {
        return BooleanStatusEnum.YES.getCode().equals(this.isNew);
    }
    
    /**
     * 判断是否热销
     * 
     * @return true-热销，false-非热销
     */
    @Transient
    public boolean getIsHotBoolean() {
        return BooleanStatusEnum.YES.getCode().equals(this.isHot);
    }
}