package com.glimmer.shopping.shoppingmall.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import com.glimmer.shopping.shoppingmall.repository.PaymentTransactionRepository;
import com.glimmer.shopping.shoppingmall.service.PaymentService;
import com.glimmer.shopping.shoppingmall.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 支付服务实现类
 * 
 * @author Glimmer
 */
@Service
@Slf4j
@DS("oracle")
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentTransactionRepository paymentRepo;

    public PaymentServiceImpl(PaymentTransactionRepository paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Override
    public Result<PaymentTransaction> createTransaction(PaymentTransaction transaction) {
        try {
            PaymentTransaction savedTransaction = paymentRepo.save(transaction);
            return Result.success(savedTransaction);
        } catch (Exception e) {
            log.error("创建支付交易失败，交易信息: {}", transaction, e);
            return Result.error("创建支付交易失败");
        }
    }
}