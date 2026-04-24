package com.glimmer.shopping.shoppingmall.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import com.glimmer.shopping.shoppingmall.repository.PaymentTransactionRepository;
import com.glimmer.shopping.shoppingmall.service.PaymentService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Glimmer
 */
@Service
@Slf4j
@DS("oracle")  // 整个类使用 Oracle
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentTransactionRepository paymentRepo;

    @Override
    public Result<PaymentTransaction> createTransaction(PaymentTransaction transaction) {
        try {
            // 保存支付交易，实体类会自动生成UUID
            PaymentTransaction savedTransaction = paymentRepo.save(transaction);
            return Result.success(savedTransaction);
        } catch (Exception e) {
            log.error("创建支付交易失败，交易信息: {}", transaction, e);
            return Result.error("创建支付交易失败");
        }
    }
}