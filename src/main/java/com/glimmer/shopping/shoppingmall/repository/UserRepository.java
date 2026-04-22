package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Glimmer
 */
public interface UserRepository extends JpaRepository<User, Long> {}
