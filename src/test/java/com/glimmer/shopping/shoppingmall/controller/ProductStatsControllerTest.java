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
public class ProductStatsControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testGetProductStatistics() throws IOException, ParseException {
        HttpPost request = new HttpPost(BASE_URL + "/api/product/stats");
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("商品统计响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetHotProducts() throws IOException, ParseException {
        String requestJson = "{\"limit\":10}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/stats/hot");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("热门商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetRecommendedProducts() throws IOException, ParseException {
        HttpPost request = new HttpPost(BASE_URL + "/api/product/stats/recommended");
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("推荐商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetNewProducts() throws IOException, ParseException {
        HttpPost request = new HttpPost(BASE_URL + "/api/product/stats/new");
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("新品列表响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetTopRatedProducts() throws IOException, ParseException {
        String requestJson = "{\"limit\":10}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/stats/rating/top");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("评分排行榜响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetMostViewedProducts() throws IOException, ParseException {
        String requestJson = "{\"limit\":10}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/stats/view/top");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("浏览最多商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetMostFavoritedProducts() throws IOException, ParseException {
        String requestJson = "{\"limit\":10}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/stats/favorite/top");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("收藏最多商品响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}