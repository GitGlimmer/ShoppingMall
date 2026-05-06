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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductQueryControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testGetProductByCategory() throws IOException, ParseException {
        String requestJson = "{\"category\":\"电子产品\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/query/category");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("按分类查询商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testSearchProducts() throws IOException, ParseException {
        String requestJson = "{\"keyword\":\"手机\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/query/search");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("搜索商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductsByPriceRange() throws IOException, ParseException {
        String requestJson = "{\"minPrice\":\"0\",\"maxPrice\":\"1000\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/query/price");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("按价格范围查询响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductsByBrand() throws IOException, ParseException {
        String requestJson = "{\"brand\":\"Apple\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/query/brand");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("按品牌查询响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductsByTag() throws IOException, ParseException {
        String requestJson = "{\"tag\":\"热销\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/query/tag");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("按标签查询响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductsByOrigin() throws IOException, ParseException {
        String requestJson = "{\"origin\":\"中国\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/query/origin");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("按产地查询响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}