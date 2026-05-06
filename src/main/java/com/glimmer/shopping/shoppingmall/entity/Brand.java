package com.glimmer.shopping.shoppingmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 品牌实体类
 */
@Data
@Entity
@Table(name = "brand", indexes = {
    @Index(name = "idx_brand_name", columnList = "name"),
    @Index(name = "idx_brand_category", columnList = "category"),
    @Index(name = "idx_brand_status", columnList = "status")
})
public class Brand {

    /**
     * 品牌ID
     */
    @Id
    @Column(length = 32)
    private String id;

    /**
     * 品牌名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 品牌Logo URL
     */
    @Column(name = "logo", length = 500)
    private String logo;

    /**
     * 品牌描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 品牌所属国家
     */
    @Column(name = "country", length = 50)
    private String country;

    /**
     * 品牌所属分类（用于筛选）
     */
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 状态（0=禁用，1=启用）
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = 1;
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}