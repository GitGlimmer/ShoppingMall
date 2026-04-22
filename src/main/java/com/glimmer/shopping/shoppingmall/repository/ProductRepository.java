package com.glimmer.shopping.shoppingmall.repository;

import com.glimmer.shopping.shoppingmall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Glimmer
 */
public interface ProductRepository extends JpaRepository<Product, Long> {}
