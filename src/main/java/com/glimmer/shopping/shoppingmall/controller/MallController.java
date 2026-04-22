package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.entity.ProductReview;
import com.glimmer.shopping.shoppingmall.service.*;
import com.glimmer.shopping.shoppingmall.util.Result;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Glimmer
 */
@RestController
@RequestMapping("/api")
@Api(tags = "商品管理")
public class MallController {

    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CartService cartService;
    @Autowired
    private AnalyticsService analyticsService;


    @ApiOperation("新增商品")
    @PostMapping("/product")
    public Result<Product> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/product/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProduct(id));
    }

    @ApiOperation("下单")
    @PostMapping("/order")
    public Result<String> createOrder(@RequestBody OrderRequest request) {
        productService.reduceStock(request.getProductId(), request.getQuantity());
        return Result.success();
    }

    @ApiOperation("支付记录")
    @PostMapping("/payment")
    public Result<PaymentTransaction> pay(@RequestBody PaymentTransaction transaction) {
        return Result.success(paymentService.createTransaction(transaction));
    }

    @ApiOperation("添加商品评论")
    @PostMapping("/review")
    public Result<ProductReview> addReview(@RequestBody ProductReview review) {
        return Result.success(reviewService.addReview(review));
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/cart/add")
    public Result addToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        cartService.addToCart(userId, productId, quantity);
        return Result.success();
    }

    @ApiOperation("获取销售分析数据")
    @GetMapping("/analytics/sales")
    public Result<List<Map<String, Object>>> getSales(@RequestParam String start, @RequestParam String end) {
        return Result.success(analyticsService.getDailySales(start, end));
    }
}
