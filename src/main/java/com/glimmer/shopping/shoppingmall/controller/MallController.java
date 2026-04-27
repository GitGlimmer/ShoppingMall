package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.CartRequest;
import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.dto.SalesQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.PaymentTransaction;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.entity.ProductInteraction;
import com.glimmer.shopping.shoppingmall.entity.ProductRating;
import com.glimmer.shopping.shoppingmall.entity.ProductReview;
import com.glimmer.shopping.shoppingmall.entity.ProductStats;
import com.glimmer.shopping.shoppingmall.service.*;
import com.glimmer.shopping.shoppingmall.util.PageResult;
import com.glimmer.shopping.shoppingmall.util.Result;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @Autowired
    private ProductInteractionService productInteractionService;
    @Autowired
    private ProductRatingService productRatingService;
    @Autowired
    private ClickHouseService clickHouseService;


    @ApiOperation("新增商品")
    @PostMapping("/product")
    public Result<String> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @ApiOperation("批量新增商品")
    @PostMapping("/product/batch")
    public Result<List<String>> addProducts(@RequestBody List<Product> products) {
        return productService.addProducts(products);
    }

    @ApiOperation("删除商品")
    @PostMapping("/product/delete")
    public Result<String> deleteProduct(@RequestBody ProductQueryRequest request) {
        return productService.deleteProduct(request);
    }

    @ApiOperation("修改商品")
    @PutMapping("/product")
    public Result<String> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @ApiOperation("获取商品详情")
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
    @PostMapping("/product/category")
    public Result<List<Product>> getProductsByCategory(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByCategory(request);
    }

    @ApiOperation("搜索商品")
    @PostMapping("/product/search")
    public Result<List<Product>> searchProducts(@RequestBody ProductQueryRequest request) {
        return productService.searchProducts(request);
    }

    @ApiOperation("按价格范围查询商品")
    @PostMapping("/product/price")
    public Result<List<Product>> getProductsByPriceRange(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByPriceRange(request);
    }

    @ApiOperation("获取热门商品")
    @PostMapping("/product/hot")
    public Result<List<Product>> getHotProducts(@RequestBody ProductQueryRequest request) {
        return productService.getHotProducts(request);
    }

    @ApiOperation("更新商品状态")
    @PostMapping("/product/status")
    public Result<String> updateProductStatus(@RequestBody ProductQueryRequest request) {
        return productService.updateProductStatus(request);
    }

    @ApiOperation("获取商品统计信息")
    @PostMapping("/product/statistics")
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
    @PostMapping("/cart")
    public Result addToCart(@RequestBody CartRequest cartRequest) {
        return cartService.addToCart(cartRequest);
    }

    @ApiOperation("获取销售数据")
    @PostMapping("/sales")
    public Result<List<Map<String, Object>>> getSales(@RequestBody SalesQueryRequest request) {
        return analyticsService.getSales(request);
    }

    @ApiOperation("获取推荐商品列表")
    @PostMapping("/product/recommended")
    public Result<List<Product>> getRecommendedProducts() {
        return productService.getRecommendedProducts();
    }

    @ApiOperation("获取新品列表")
    @PostMapping("/product/new")
    public Result<List<Product>> getNewProducts() {
        return productService.getNewProducts();
    }

    @ApiOperation("按品牌查询商品")
    @PostMapping("/product/brand")
    public Result<List<Product>> getProductsByBrand(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByBrand(request.getBrand());
    }

    @ApiOperation("按标签查询商品")
    @PostMapping("/product/tag")
    public Result<List<Product>> getProductsByTag(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByTag(request.getTag());
    }

    @ApiOperation("按产地查询商品")
    @PostMapping("/product/origin")
    public Result<List<Product>> getProductsByOrigin(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByOrigin(request.getOrigin());
    }

    @ApiOperation("获取商品评分排行榜")
    @PostMapping("/product/rating/top")
    public Result<List<Product>> getTopRatedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getTopRatedProducts(request.getLimit());
    }

    @ApiOperation("获取浏览最多的商品")
    @PostMapping("/product/view/top")
    public Result<List<Product>> getMostViewedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getMostViewedProducts(request.getLimit());
    }

    @ApiOperation("获取收藏最多的商品")
    @PostMapping("/product/favorite/top")
    public Result<List<Product>> getMostFavoritedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getMostFavoritedProducts(request.getLimit());
    }

    @ApiOperation("更新商品评分")
    @PutMapping("/product/rating")
    public Result<String> updateProductRating(@RequestBody ProductQueryRequest request) {
        return productService.updateProductRating(request.getId(), request.getRating());
    }

    @ApiOperation("增加商品浏览次数")
    @PostMapping("/product/view")
    public Result<String> incrementViewCount(@RequestBody ProductQueryRequest request) {
        return productService.incrementViewCount(request.getId());
    }

    @ApiOperation("增加商品收藏次数")
    @PostMapping("/product/favorite")
    public Result<String> incrementFavoriteCount(@RequestBody ProductQueryRequest request) {
        return productService.incrementFavoriteCount(request.getId());
    }

    @ApiOperation("设置商品推荐状态")
    @PutMapping("/product/recommended")
    public Result<String> setRecommendedStatus(@RequestBody ProductQueryRequest request) {
        return productService.setRecommendedStatus(request.getId(), request.getIsRecommended());
    }

    @ApiOperation("设置商品新品状态")
    @PutMapping("/product/new/status")
    public Result<String> setNewStatus(@RequestBody ProductQueryRequest request) {
        return productService.setNewStatus(request.getId(), request.getIsNew());
    }

    @ApiOperation("设置商品热销状态")
    @PutMapping("/product/hot/status")
    public Result<String> setHotStatus(@RequestBody ProductQueryRequest request) {
        return productService.setHotStatus(request.getId(), request.getIsHot());
    }

    @ApiOperation("更新商品排序权重")
    @PutMapping("/product/sort")
    public Result<String> updateSortWeight(@RequestBody ProductQueryRequest request) {
        return productService.updateSortWeight(request.getId(), request.getLimit());
    }

    @ApiOperation("获取商品详情（包含浏览计数）")
    @PostMapping("/product/detail/views")
    public Result<Product> getProductDetail(@RequestBody ProductQueryRequest request) {
        return productService.getProductDetail(request);
    }

    @ApiOperation("记录用户互动")
    @PostMapping("/interaction")
    public Result<String> recordInteraction(@RequestBody ProductQueryRequest request) {
        return productInteractionService.recordInteraction(
                request.getId(),
                request.getUserId(),
                request.getInteractionType(),
                request.getClientIp(),
                request.getUserAgent(),
                request.getSessionId()
        );
    }

    @ApiOperation("获取商品互动统计")
    @PostMapping("/interaction/product/stats")
    public Result<Map<String, Long>> getProductInteractionStats(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getProductInteractionStats(request.getId());
    }

    @ApiOperation("获取商品指定类型互动次数")
    @PostMapping("/interaction/count")
    public Result<Long> getInteractionCount(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getInteractionCount(request.getId(), request.getInteractionType());
    }

    @ApiOperation("获取用户互动记录")
    @PostMapping("/interaction/user")
    public Result<List<ProductInteraction>> getUserInteractions(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getUserInteractions(request.getUserId(), request.getInteractionType());
    }

    @ApiOperation("检查用户是否已互动")
    @PostMapping("/interaction/check")
    public Result<Boolean> hasUserInteracted(@RequestBody ProductQueryRequest request) {
        return productInteractionService.hasUserInteracted(request.getId(), request.getUserId(), request.getInteractionType());
    }

    @ApiOperation("移除用户互动")
    @DeleteMapping("/interaction")
    public Result<String> removeInteraction(@RequestBody ProductQueryRequest request) {
        return productInteractionService.removeInteraction(request.getId(), request.getUserId(), request.getInteractionType());
    }

    @ApiOperation("获取最近互动记录")
    @PostMapping("/interaction/product/recent")
    public PageResult<ProductInteraction> getRecentInteractions(@RequestBody ProductQueryRequest request) {
        return productInteractionService.getRecentInteractions(request.getId(), request.getLimit());
    }

    @ApiOperation("添加或更新商品评分")
    @PostMapping("/rating")
    public Result<String> addOrUpdateRating(@RequestBody ProductQueryRequest request) {
        return productRatingService.addOrUpdateRating(
                request.getId(),
                request.getUserId(),
                request.getRating(),
                request.getComment(),
                request.getIsAnonymous(),
                request.getClientIp(),
                request.getUserAgent()
        );
    }

    @ApiOperation("获取商品平均评分")
    @PostMapping("/rating/product/average")
    public Result<Double> getAverageRating(@RequestBody ProductQueryRequest request) {
        return productRatingService.getAverageRating(request.getId());
    }

    @ApiOperation("获取商品评分统计")
    @PostMapping("/rating/product/stats")
    public Result<Map<String, Object>> getRatingStatistics(@RequestBody ProductQueryRequest request) {
        return productRatingService.getRatingStatistics(request.getId());
    }

    @ApiOperation("获取用户评分记录")
    @PostMapping("/rating/user")
    public Result<List<ProductRating>> getUserRatings(@RequestBody ProductQueryRequest request) {
        return productRatingService.getUserRatings(request.getUserId());
    }

    @ApiOperation("获取商品评分列表")
    @PostMapping("/rating/product/list")
    public PageResult<ProductRating> getProductRatings(@RequestBody ProductQueryRequest request) {
        return productRatingService.getProductRatings(request.getId(), request.getPage(), request.getSize());
    }

    @ApiOperation("检查用户是否已评分")
    @PostMapping("/rating/check")
    public Result<Boolean> hasUserRated(@RequestBody ProductQueryRequest request) {
        return productRatingService.hasUserRated(request.getId(), request.getUserId());
    }

    @ApiOperation("删除评分记录")
    @DeleteMapping("/rating")
    public Result<String> deleteRating(@RequestBody ProductQueryRequest request) {
        return productRatingService.deleteRating(request.getId(), request.getUserId());
    }

    @ApiOperation("获取热门评分商品")
    @PostMapping("/rating/top")
    public Result<List<Map<String, Object>>> getTopRatedProductsList(@RequestBody ProductQueryRequest request) {
        return productRatingService.getTopRatedProducts(request.getLimit());
    }

    @ApiOperation("获取最新评分记录")
    @PostMapping("/rating/recent")
    public Result<List<ProductRating>> getRecentRatings(@RequestBody ProductQueryRequest request) {
        return productRatingService.getRecentRatings(request.getLimit());
    }

    @ApiOperation("获取商品历史统计数据")
    @PostMapping("/stats/product/history")
    public Result<List<ProductStats>> getProductStatsHistory(@RequestBody ProductQueryRequest request) {
        return Result.success(clickHouseService.getProductStatsHistory(
                request.getId(),
                request.getMinRating() != null ? LocalDate.now().minusDays(request.getMinRating().longValue()) : LocalDate.now().minusDays(30),
                LocalDate.now()
        ));
    }

    @ApiOperation("获取分类销售统计")
    @PostMapping("/stats/category/sales")
    public Result<List<ProductStats>> getCategorySalesStats(@RequestBody ProductQueryRequest request) {
        return Result.success(clickHouseService.getCategorySalesStats(LocalDate.now()));
    }
}