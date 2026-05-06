package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import com.glimmer.shopping.shoppingmall.service.PaymentService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@Api(tags = "支付管理")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @ApiOperation("创建支付交易")
    @PostMapping
    public Result<PaymentTransaction> pay(@RequestBody PaymentTransaction transaction) {
        return paymentService.createTransaction(transaction);
    }
}