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
    @PostMapping("/addProduct")
    public Result<String> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @ApiOperation("批量新增商品")
    @PostMapping("/addProducts")
    public Result<List<String>> addProducts(@RequestBody List<Product> products) {
        return productService.addProducts(products);
    }

    @ApiOperation("删除商品")
    @DeleteMapping("/deleteProduct/{id}")
    public Result<String> deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id);
    }

    @ApiOperation("修改商品")
    @PutMapping("/updateProduct")
    public Result<String> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/product/{id}")
    public Result<Product> getProduct(@PathVariable String id) {
        return productService.getProduct(id);
    }

    @ApiOperation("获取商品列表")
    @GetMapping("/productList")
    public Result<List<Product>> getProductList() {
        return productService.getProductList();
    }

    @ApiOperation("下单")
    @PostMapping("/order")
    public Result<String> createOrder(@RequestBody OrderRequest request) {
        return productService.createOrder(request);
    }

    @ApiOperation("支付记录")
    @PostMapping("/payment")
    public Result<PaymentTransaction> pay(@RequestBody PaymentTransaction transaction) {
        return paymentService.createTransaction(transaction);
    }

    @ApiOperation("添加商品评论")
    @PostMapping("/review")
    public Result<ProductReview> addReview(@RequestBody ProductReview review) {
        return reviewService.addReview(review);
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/cart/add")
    public Result addToCart(@RequestParam String userId, @RequestParam String productId, @RequestParam Integer quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @ApiOperation("获取销售分析数据")
    @GetMapping("/analytics/sales")
    public Result<List<Map<String, Object>>> getSales(@RequestParam String start, @RequestParam String end) {
        return analyticsService.getDailySales(start, end);
    }
}