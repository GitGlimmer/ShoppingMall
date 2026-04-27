package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.CartRequest;
import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.dto.SalesQueryRequest;
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
    @PostMapping("/deleteProduct")
    public Result<String> deleteProduct(@RequestBody ProductQueryRequest request) {
        return productService.deleteProduct(request);
    }

    @ApiOperation("修改商品")
    @PutMapping("/updateProduct")
    public Result<String> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @ApiOperation("获取商品详情（基础信息）")
    @PostMapping("/product/detail")
    public Result<Product> getProduct(@RequestBody ProductQueryRequest request) {
        return productService.getProduct(request);
    }

    @ApiOperation("获取商品列表")
    @PostMapping("/product/list")
    public Result<List<Product>> getProductList() {
        return productService.getProductList();
    }

    @ApiOperation("按分类查询商品")
    @PostMapping("/products/by-category")
    public Result<List<Product>> getProductsByCategory(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByCategory(request);
    }

    @ApiOperation("搜索商品")
    @PostMapping("/products/search")
    public Result<List<Product>> searchProducts(@RequestBody ProductQueryRequest request) {
        return productService.searchProducts(request);
    }

    @ApiOperation("按价格范围查询商品")
    @PostMapping("/products/by-price-range")
    public Result<List<Product>> getProductsByPriceRange(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByPriceRange(request);
    }

    @ApiOperation("获取热门商品")
    @PostMapping("/products/hot")
    public Result<List<Product>> getHotProducts(@RequestBody ProductQueryRequest request) {
        return productService.getHotProducts(request);
    }

    @ApiOperation("更新商品状态")
    @PostMapping("/product/update-status")
    public Result<String> updateProductStatus(@RequestBody ProductQueryRequest request) {
        return productService.updateProductStatus(request);
    }

    @ApiOperation("获取商品统计信息")
    @PostMapping("/products/statistics")
    public Result<Map<String, Object>> getProductStatistics() {
        return productService.getProductStatistics();
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
    public Result addToCart(@RequestBody CartRequest cartRequest) {
        return cartService.addToCart(cartRequest);
    }

    @ApiOperation("获取销售数据")
    @PostMapping("/sales")
    public Result<List<Map<String, Object>>> getSales(@RequestBody SalesQueryRequest request) {
        return analyticsService.getSales(request);
    }

    @ApiOperation("获取推荐商品列表")
    @GetMapping("/products/recommended")
    public Result<List<Product>> getRecommendedProducts() {
        return productService.getRecommendedProducts();
    }

    @ApiOperation("获取新品列表")
    @GetMapping("/products/new")
    public Result<List<Product>> getNewProducts() {
        return productService.getNewProducts();
    }

    @ApiOperation("按品牌查询商品")
    @GetMapping("/products/by-brand/{brand}")
    public Result<List<Product>> getProductsByBrand(@PathVariable String brand) {
        return productService.getProductsByBrand(brand);
    }

    @ApiOperation("按标签查询商品")
    @GetMapping("/products/by-tag/{tag}")
    public Result<List<Product>> getProductsByTag(@PathVariable String tag) {
        return productService.getProductsByTag(tag);
    }

    @ApiOperation("按产地查询商品")
    @GetMapping("/products/by-origin/{origin}")
    public Result<List<Product>> getProductsByOrigin(@PathVariable String origin) {
        return productService.getProductsByOrigin(origin);
    }

    @ApiOperation("获取商品评分排行榜")
    @GetMapping("/products/top-rated")
    public Result<List<Product>> getTopRatedProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return productService.getTopRatedProducts(limit);
    }

    @ApiOperation("获取浏览最多的商品")
    @GetMapping("/products/most-viewed")
    public Result<List<Product>> getMostViewedProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return productService.getMostViewedProducts(limit);
    }

    @ApiOperation("获取收藏最多的商品")
    @GetMapping("/products/most-favorited")
    public Result<List<Product>> getMostFavoritedProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return productService.getMostFavoritedProducts(limit);
    }

    @ApiOperation("更新商品评分")
    @PutMapping("/product/{productId}/rating")
    public Result<String> updateProductRating(@PathVariable String productId, @RequestParam Double rating) {
        return productService.updateProductRating(productId, rating);
    }

    @ApiOperation("增加商品浏览次数")
    @PostMapping("/product/{productId}/view")
    public Result<String> incrementViewCount(@PathVariable String productId) {
        return productService.incrementViewCount(productId);
    }

    @ApiOperation("增加商品收藏次数")
    @PostMapping("/product/{productId}/favorite")
    public Result<String> incrementFavoriteCount(@PathVariable String productId) {
        return productService.incrementFavoriteCount(productId);
    }

    @ApiOperation("设置商品推荐状态")
    @PutMapping("/product/{productId}/recommended")
    public Result<String> setRecommendedStatus(@PathVariable String productId, @RequestParam Boolean isRecommended) {
        return productService.setRecommendedStatus(productId, isRecommended);
    }

    @ApiOperation("设置商品新品状态")
    @PutMapping("/product/{productId}/new")
    public Result<String> setNewStatus(@PathVariable String productId, @RequestParam Boolean isNew) {
        return productService.setNewStatus(productId, isNew);
    }

    @ApiOperation("设置商品热销状态")
    @PutMapping("/product/{productId}/hot")
    public Result<String> setHotStatus(@PathVariable String productId, @RequestParam Boolean isHot) {
        return productService.setHotStatus(productId, isHot);
    }

    @ApiOperation("更新商品排序权重")
    @PutMapping("/product/{productId}/sort-weight")
    public Result<String> updateSortWeight(@PathVariable String productId, @RequestParam Integer sortWeight) {
        return productService.updateSortWeight(productId, sortWeight);
    }

    @ApiOperation("获取商品详情（包含浏览计数）")
    @PostMapping("/product/detail-with-views")
    public Result<Product> getProductDetail(@RequestBody ProductQueryRequest request) {
        return productService.getProductDetail(request);
    }
}