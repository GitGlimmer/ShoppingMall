package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * @author Glimmer
 */
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 统计在线用户数量
     * 包括：isOnline = true 的用户，以及 isOnline = false 但最后活跃时间在1小时内的用户
     *
     * @param now 当前时间
     * @return 在线用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isOnline = true OR (u.isOnline = false AND u.lastActiveTime >= :thresholdTime)")
    long countActiveUsers(@Param("thresholdTime") LocalDateTime thresholdTime);
    
    /**
     * 统计 isOnline = true 的用户数量
     *
     * @param isOnline 是否在线
     * @return 用户数量
     */
    long countByIsOnline(Boolean isOnline);
}