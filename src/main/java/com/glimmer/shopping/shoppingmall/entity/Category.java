package com.glimmer.shopping.shoppingmall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类实体类（自关联实现父子结构）
 */
@Data
@Entity
@Table(name = "category", indexes = {
    @Index(name = "idx_category_parent_id", columnList = "parent_id"),
    @Index(name = "idx_category_level", columnList = "level"),
    @Index(name = "idx_category_status", columnList = "status")
})
public class Category {

    /**
     * 分类ID
     */
    @Id
    @Column(length = 32)
    private String id;

    /**
     * 分类名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 父分类ID（顶级分类为null或空字符串）
     */
    @Column(name = "parent_id", length = 32)
    private String parentId;

    /**
     * 父分类（关联关系）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Category parent;

    /**
     * 子分类列表
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<Category> children = new ArrayList<>();

    /**
     * 分类层级（1-顶级，2-二级，3-三级）
     */
    @Column(name = "level", nullable = false)
    private Integer level;

    /**
     * 分类图标
     */
    @Column(name = "icon", length = 200)
    private String icon;

    /**
     * 分类描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 状态（0-禁用，1-启用）
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 是否热门分类
     */
    @Column(name = "is_hot")
    private Integer isHot;

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
        if (level == null) {
            level = 1;
        }
        if (isHot == null) {
            isHot = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}