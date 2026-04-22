package com.glimmer.shopping.shoppingmall.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import com.glimmer.shopping.shoppingmall.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DS("oracle")  // 整个类使用 Oracle
public class PaymentService {
    @Autowired
    private PaymentTransactionRepository paymentRepo;

    public PaymentTransaction createTransaction(PaymentTransaction transaction) {
        return paymentRepo.save(transaction);
    }
}
