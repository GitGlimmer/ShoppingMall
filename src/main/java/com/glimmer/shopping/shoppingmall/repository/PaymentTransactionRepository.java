package com.glimmer.shopping.shoppingmall.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Glimmer
 */
@Repository
@DS("oracle")
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
}