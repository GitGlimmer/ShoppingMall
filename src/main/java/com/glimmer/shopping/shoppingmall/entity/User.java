package com.glimmer.shopping.shoppingmall.entity;

import com.glimmer.shopping.shoppingmall.util.UUIDUtil;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 存储用户的基本信息，包括用户名、密码、邮箱、手机号等
 * 支持在线状态追踪和最后活跃时间记录
 * 
 * @author Glimmer
 */
@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_phone", columnList = "phone"),
    @Index(name = "idx_user_is_online", columnList = "is_online"),
    @Index(name = "idx_user_last_active_time", columnList = "last_active_time")
})
@Data
public class User {
    
    /** 用户唯一标识ID */
    @Id
    @Column(name = "id", length = 36, nullable = false, columnDefinition = "VARCHAR(36) COMMENT '用户唯一标识ID'")
    private String id;
    
    /** 用户昵称/用户名 */
    @Column(name = "username", length = 100, nullable = false, columnDefinition = "VARCHAR(100) NOT NULL COMMENT '用户昵称/用户名'")
    private String username;
    
    /** 用户密码（加密存储） */
    @Column(name = "password", length = 255, columnDefinition = "VARCHAR(255) COMMENT '用户密码（加密存储）'")
    private String password;
    
    /** 用户邮箱地址 */
    @Column(name = "email", length = 200, columnDefinition = "VARCHAR(200) COMMENT '用户邮箱地址'")
    private String email;
    
    /** 用户手机号码 */
    @Column(name = "phone", length = 20, columnDefinition = "VARCHAR(20) COMMENT '用户手机号码'")
    private String phone;
    
    /** 是否在线：TRUE-在线，FALSE-离线 */
    @Column(name = "is_online", columnDefinition = "BOOLEAN DEFAULT FALSE COMMENT '是否在线：TRUE-在线，FALSE-离线'")
    private Boolean isOnline;
    
    /** 最后活跃时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_active_time", columnDefinition = "DATETIME COMMENT '最后活跃时间'")
    private LocalDateTime lastActiveTime;
    
    /** 用户创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间'")
    private LocalDateTime createTime;
    
    /** 用户更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户更新时间'")
    private LocalDateTime updateTime;
    
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
        if (this.isOnline == null) {
            this.isOnline = false;
        }
        this.updateTime = LocalDateTime.now();
    }
}