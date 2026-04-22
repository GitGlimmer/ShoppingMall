package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Glimmer
 */
public interface OrderRepository extends JpaRepository<Order, Long> {}
