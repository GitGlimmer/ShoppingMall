package com.glimmer.shopping.shoppingmall.controller;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductManageControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testGetProductList() throws IOException, ParseException {
        HttpPost request = new HttpPost(BASE_URL + "/api/product/manage/list");
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取商品列表响应: " + responseBody);

            assertNotNull(responseBody);
            assertTrue(responseBody.contains("\"code\":200") || responseBody.contains("\"code\": 200"));
        }
    }

    @Test
    public void testAddProduct() throws IOException, ParseException {
        String randomId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String productJson = String.format(
            "{\"id\":\"%s\",\"name\":\"测试商品-%s\",\"description\":\"测试商品描述\",\"price\":\"99.99\",\"stock\":\"100\",\"category\":\"电子产品\",\"status\":\"ON_SALE\",\"sales\":0,\"imageUrl\":\"http://example.com/image.jpg\"}",
            randomId, randomId
        );

        HttpPost request = new HttpPost(BASE_URL + "/api/product/manage");
        request.setEntity(new StringEntity(productJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("新增商品响应: " + responseBody);

            assertNotNull(responseBody);
            assertTrue(responseBody.contains("\"code\":200") || responseBody.contains("\"code\": 200"));
        }
    }

    @Test
    public void testAddProductsBatch() throws IOException, ParseException {
        String productJson = "[{\"name\":\"批量商品1\",\"description\":\"批量1\",\"price\":\"100.00\",\"stock\":\"50\",\"category\":\"批量\"},{\"name\":\"批量商品2\",\"description\":\"批量2\",\"price\":\"100.00\",\"stock\":\"50\",\"category\":\"批量\"}]";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/manage/batch");
        request.setEntity(new StringEntity(productJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("批量新增商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testDeleteProduct() throws IOException, ParseException {
        String randomId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String productJson = String.format(
            "{\"id\":\"%s\",\"name\":\"待删除商品\"}",
            randomId
        );

        HttpPost request = new HttpPost(BASE_URL + "/api/product/manage/delete");
        request.setEntity(new StringEntity(productJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("删除商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testUpdateProduct() throws IOException, ParseException {
        String randomId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String productJson = String.format(
            "{\"id\":\"%s\",\"name\":\"修改后的商品名称\",\"price\":\"199.99\"}",
            randomId
        );

        HttpPut request = new HttpPut(BASE_URL + "/api/product/manage");
        request.setEntity(new StringEntity(productJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("修改商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductDetail() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/manage/detail");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取商品详情响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductDetailWithViews() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/manage/detail/views");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取商品详情(含浏览计数)响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}