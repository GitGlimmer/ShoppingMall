package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import com.glimmer.shopping.shoppingmall.util.Result;

/**
 * @author Glimmer
 */
public interface PaymentService {
    
    /**
     * 创建支付交易
     *
     * @param transaction
     * @return
     */
    Result<PaymentTransaction> createTransaction(PaymentTransaction transaction);
}