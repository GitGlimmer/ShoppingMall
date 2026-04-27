package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.dto.ProductQueryRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;
import java.util.Map;

/**
 * @author Glimmer
 */
public interface ProductService {
    /**
     * 获取商品详情
     *
     * @param request 查询请求
     * @return
     */
    Result<Product> getProduct(ProductQueryRequest request);

    /**
     * 更新库存
     *
     * @param id
     * @param quantity
     * @return
     */
    Result<Boolean> reduceStock(String id, int quantity);

    /**
     * 新增商品
     *
     * @param product
     * @return 新增成功商品的id
     */
    Result<String> addProduct(Product product);

    /**
     * 批量新增商品
     *
     * @param products
     * @return 新增成功商品的id列表
     */
    Result<List<String>> addProducts(List<Product> products);

    /**
     * 修改商品
     *
     * @param product
     * @return 修改成功商品的id
     */
    Result<String> updateProduct(Product product);

    /**
     * 删除商品
     *
     * @param request 查询请求
     * @return 已删除商品的id
     */
    Result<String> deleteProduct(ProductQueryRequest request);

    /**
     * 获取商品列表
     *
     * @return
     */
    Result<List<Product>> getProductList();

    /**
     * 按分类查询商品
     *
     * @param request 查询请求
     * @return
     */
    Result<List<Product>> getProductsByCategory(ProductQueryRequest request);

    /**
     * 按关键字搜索商品
     *
     * @param request 查询请求
     * @return
     */
    Result<List<Product>> searchProducts(ProductQueryRequest request);

    /**
     * 按价格范围查询商品
     *
     * @param request 查询请求
     * @return
     */
    Result<List<Product>> getProductsByPriceRange(ProductQueryRequest request);

    /**
     * 获取热门商品（按销量排序）
     *
     * @param request 查询请求
     * @return
     */
    Result<List<Product>> getHotProducts(ProductQueryRequest request);

    /**
     * 更新商品状态（上架/下架）
     *
     * @param request 查询请求
     * @return
     */
    Result<String> updateProductStatus(ProductQueryRequest request);

    /**
     * 获取商品统计信息
     *
     * @return
     */
    Result<Map<String, Object>> getProductStatistics();

    /**
     * 下单
     *
     * @param request
     * @return
     */
    Result<String> createOrder(OrderRequest request);
    
    /**
     * 获取推荐商品列表
     *
     * @return
     */
    Result<List<Product>> getRecommendedProducts();
    
    /**
     * 获取新品列表
     *
     * @return
     */
    Result<List<Product>> getNewProducts();
    
    /**
     * 按品牌查询商品
     *
     * @param brand 品牌名称
     * @return
     */
    Result<List<Product>> getProductsByBrand(String brand);
    
    /**
     * 按标签查询商品
     *
     * @param tag 标签名称
     * @return
     */
    Result<List<Product>> getProductsByTag(String tag);
    
    /**
     * 按产地查询商品
     *
     * @param origin 产地
     * @return
     */
    Result<List<Product>> getProductsByOrigin(String origin);
    
    /**
     * 获取商品评分排行榜
     *
     * @param limit 限制数量
     * @return
     */
    Result<List<Product>> getTopRatedProducts(Integer limit);
    
    /**
     * 获取浏览最多的商品
     *
     * @param limit 限制数量
     * @return
     */
    Result<List<Product>> getMostViewedProducts(Integer limit);
    
    /**
     * 获取收藏最多的商品
     *
     * @param limit 限制数量
     * @return
     */
    Result<List<Product>> getMostFavoritedProducts(Integer limit);
    
    /**
     * 更新商品评分
     *
     * @param productId 商品ID
     * @param rating 新评分
     * @return
     */
    Result<String> updateProductRating(String productId, Double rating);
    
    /**
     * 增加商品浏览次数
     *
     * @param productId 商品ID
     * @return
     */
    Result<String> incrementViewCount(String productId);
    
    /**
     * 增加商品收藏次数
     *
     * @param productId 商品ID
     * @return
     */
    Result<String> incrementFavoriteCount(String productId);
    
    /**
     * 设置商品推荐状态
     *
     * @param productId 商品ID
     * @param isRecommended 是否推荐
     * @return
     */
    Result<String> setRecommendedStatus(String productId, Boolean isRecommended);
    
    /**
     * 设置商品新品状态
     *
     * @param productId 商品ID
     * @param isNew 是否新品
     * @return
     */
    Result<String> setNewStatus(String productId, Boolean isNew);
    
    /**
     * 设置商品热销状态
     *
     * @param productId 商品ID
     * @param isHot 是否热销
     * @return
     */
    Result<String> setHotStatus(String productId, Boolean isHot);
    
    /**
     * 更新商品排序权重
     *
     * @param productId 商品ID
     * @param sortWeight 排序权重
     * @return
     */
    Result<String> updateSortWeight(String productId, Integer sortWeight);
    
    /**
     * 获取商品详情（包含浏览计数）
     *
     * @param request 查询请求
     * @return
     */
    Result<Product> getProductDetail(ProductQueryRequest request);
}