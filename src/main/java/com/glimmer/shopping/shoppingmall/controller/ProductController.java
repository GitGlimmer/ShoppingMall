package com.glimmer.shopping.shoppingmall.controller;

import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.service.ProductService;
import com.glimmer.shopping.shoppingmall.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@Api(tags = "商品管理")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("新增商品")
    @PostMapping
    public Result<String> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @ApiOperation("批量新增商品")
    @PostMapping("/batch")
    public Result<List<String>> addProducts(@RequestBody List<Product> products) {
        return productService.addProducts(products);
    }

    @ApiOperation("删除商品")
    @PostMapping("/delete")
    public Result<String> deleteProduct(@RequestBody ProductQueryRequest request) {
        return productService.deleteProduct(request);
    }

    @ApiOperation("修改商品")
    @PutMapping
    public Result<String> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @ApiOperation("获取商品详情")
    @PostMapping("/detail")
    public Result<Product> getProduct(@RequestBody ProductQueryRequest request) {
        return productService.getProduct(request);
    }

    @ApiOperation("获取商品列表")
    @PostMapping("/list")
    public Result<List<Product>> getProductList() {
        return productService.getProductList();
    }

    @ApiOperation("按分类查询商品")
    @PostMapping("/category")
    public Result<List<Product>> getProductsByCategory(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByCategory(request);
    }

    @ApiOperation("搜索商品")
    @PostMapping("/search")
    public Result<List<Product>> searchProducts(@RequestBody ProductQueryRequest request) {
        return productService.searchProducts(request);
    }

    @ApiOperation("按价格范围查询商品")
    @PostMapping("/price")
    public Result<List<Product>> getProductsByPriceRange(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByPriceRange(request);
    }

    @ApiOperation("获取热门商品")
    @PostMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestBody ProductQueryRequest request) {
        return productService.getHotProducts(request);
    }

    @ApiOperation("更新商品状态")
    @PostMapping("/status")
    public Result<String> updateProductStatus(@RequestBody ProductQueryRequest request) {
        return productService.updateProductStatus(request);
    }

    @ApiOperation("获取商品统计信息")
    @PostMapping("/statistics")
    public Result<Map<String, Object>> getProductStatistics() {
        return productService.getProductStatistics();
    }

    @ApiOperation("获取推荐商品列表")
    @PostMapping("/recommended")
    public Result<List<Product>> getRecommendedProducts() {
        return productService.getRecommendedProducts();
    }

    @ApiOperation("获取新品列表")
    @PostMapping("/new")
    public Result<List<Product>> getNewProducts() {
        return productService.getNewProducts();
    }

    @ApiOperation("按品牌查询商品")
    @PostMapping("/brand")
    public Result<List<Product>> getProductsByBrand(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByBrand(request.getBrand());
    }

    @ApiOperation("按标签查询商品")
    @PostMapping("/tag")
    public Result<List<Product>> getProductsByTag(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByTag(request.getTag());
    }

    @ApiOperation("按产地查询商品")
    @PostMapping("/origin")
    public Result<List<Product>> getProductsByOrigin(@RequestBody ProductQueryRequest request) {
        return productService.getProductsByOrigin(request.getOrigin());
    }

    @ApiOperation("获取商品评分排行榜")
    @PostMapping("/rating/top")
    public Result<List<Product>> getTopRatedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getTopRatedProducts(request.getLimit());
    }

    @ApiOperation("获取浏览最多的商品")
    @PostMapping("/view/top")
    public Result<List<Product>> getMostViewedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getMostViewedProducts(request.getLimit());
    }

    @ApiOperation("获取收藏最多的商品")
    @PostMapping("/favorite/top")
    public Result<List<Product>> getMostFavoritedProducts(@RequestBody ProductQueryRequest request) {
        return productService.getMostFavoritedProducts(request.getLimit());
    }

    @ApiOperation("更新商品评分")
    @PutMapping("/rating")
    public Result<String> updateProductRating(@RequestBody ProductQueryRequest request) {
        return productService.updateProductRating(request.getId(), request.getRating());
    }

    @ApiOperation("增加商品浏览次数")
    @PostMapping("/view")
    public Result<String> incrementViewCount(@RequestBody ProductQueryRequest request) {
        return productService.incrementViewCount(request.getId());
    }

    @ApiOperation("增加商品收藏次数")
    @PostMapping("/favorite")
    public Result<String> incrementFavoriteCount(@RequestBody ProductQueryRequest request) {
        return productService.incrementFavoriteCount(request.getId());
    }

    @ApiOperation("设置商品推荐状态")
    @PutMapping("/recommended")
    public Result<String> setRecommendedStatus(@RequestBody ProductQueryRequest request) {
        return productService.setRecommendedStatus(request.getId(), request.getIsRecommended());
    }

    @ApiOperation("设置商品新品状态")
    @PutMapping("/new/status")
    public Result<String> setNewStatus(@RequestBody ProductQueryRequest request) {
        return productService.setNewStatus(request.getId(), request.getIsNew());
    }

    @ApiOperation("设置商品热销状态")
    @PutMapping("/hot/status")
    public Result<String> setHotStatus(@RequestBody ProductQueryRequest request) {
        return productService.setHotStatus(request.getId(), request.getIsHot());
    }

    @ApiOperation("更新商品排序权重")
    @PutMapping("/sort")
    public Result<String> updateSortWeight(@RequestBody ProductQueryRequest request) {
        return productService.updateSortWeight(request.getId(), request.getLimit());
    }

    @ApiOperation("获取商品详情（包含浏览计数）")
    @PostMapping("/detail/views")
    public Result<Product> getProductDetail(@RequestBody ProductQueryRequest request) {
        return productService.getProductDetail(request);
    }
}