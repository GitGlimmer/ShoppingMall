package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@Api(tags = "订单管理")
public class OrderController {

    @Autowired
    private ProductService productService;

    @ApiOperation("下单")
    @PostMapping
    public Result<String> createOrder(@RequestBody OrderRequest request) {
        return productService.createOrder(request);
    }
}