package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.CartRequest;
import com.glimmer.shopping.shoppingmall.service.CartService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@Api(tags = "购物车管理")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping
    public Result addToCart(@RequestBody CartRequest cartRequest) {
        return cartService.addToCart(cartRequest);
    }

    @ApiOperation("获取购物车内容")
    @GetMapping("/{userId}")
    public Result<Map<String, Integer>> getCart(@PathVariable String userId) {
        return cartService.getCart(userId);
    }
}