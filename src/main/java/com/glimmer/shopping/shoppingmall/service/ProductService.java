package com.glimmer.shopping.shoppingmall.service;

import com.glimmer.shopping.shoppingmall.dto.OrderRequest;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.util.Result;

import java.util.List;

/**
 * @author Glimmer
 */
public interface ProductService {
    /**
     * 查询商品（带缓存）
     *
     * @param id
     * @return
     */
    Result<Product> getProduct(String id);

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
     * @param id
     * @return 已删除商品的id
     */
    Result<String> deleteProduct(String id);

    /**
     * 获取商品列表
     *
     * @return
     */
    Result<List<Product>> getProductList();

    /**
     * 下单
     *
     * @param request
     * @return
     */
    Result<String> createOrder(OrderRequest request);

}