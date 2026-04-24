package com.glimmer.shopping.shoppingmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glimmer.shopping.shoppingmall.entity.Product;
import com.glimmer.shopping.shoppingmall.util.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 实时API测试类 - 针对已启动的服务进行测试
 * 使用前请确保服务已启动在 http://localhost:8080
 */
public class MallControllerTest {

    private final String baseUrl = "http://localhost:8080/api";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testLiveAddProduct() {
        System.out.println("=== 开始实时API测试 ===");
        
        // 1. 新增商品
        Product product = new Product();
        product.setName("实时测试商品");
        product.setPrice("66.66");
        product.setStock(25);
        product.setDescription("这是实时测试商品");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(product, headers);

        ResponseEntity<Result> addResponse = restTemplate.postForEntity(
            baseUrl + "/addProduct", request, Result.class);

        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        assertNotNull(addResponse.getBody());
        assertEquals(200, addResponse.getBody().getCode());
        
        System.out.println("✅ 新增商品成功: " + addResponse.getBody());

        // 2. 获取商品列表
        ResponseEntity<Result> listResponse = restTemplate.getForEntity(
            baseUrl + "/productList", Result.class);

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertEquals(200, listResponse.getBody().getCode());
        
        System.out.println("✅ 获取商品列表成功: " + listResponse.getBody());

        // 3. 获取商品详情（假设新增的商品ID为1）
        ResponseEntity<Result> detailResponse = restTemplate.getForEntity(
            baseUrl + "/product/1", Result.class);

        assertEquals(HttpStatus.OK, detailResponse.getStatusCode());
        assertNotNull(detailResponse.getBody());
        assertEquals(200, detailResponse.getBody().getCode());
        
        System.out.println("✅ 获取商品详情成功: " + detailResponse.getBody());

        // 4. 修改商品
        product.setId(1L);
        product.setName("修改后的实时测试商品");
        product.setPrice("77.77");
        product.setStock(15);

        ResponseEntity<Result> updateResponse = restTemplate.exchange(
            baseUrl + "/updateProduct", HttpMethod.PUT, request, Result.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(200, updateResponse.getBody().getCode());
        
        System.out.println("✅ 修改商品成功: " + updateResponse.getBody());

        // 5. 删除商品
        ResponseEntity<Result> deleteResponse = restTemplate.exchange(
            baseUrl + "/deleteProduct/1", HttpMethod.DELETE, null, Result.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertEquals(200, deleteResponse.getBody().getCode());
        
        System.out.println("✅ 删除商品成功: " + deleteResponse.getBody());
        
        System.out.println("=== 实时API测试完成 ===");
    }
}
