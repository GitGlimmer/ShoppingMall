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
public class AnalyticsControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testGetSales() throws IOException, ParseException {
        String requestJson = "{\"startDate\":\"2024-01-01\",\"endDate\":\"2024-12-31\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/analytics/sales");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("销售数据响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductStatsHistory() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\",\"minRating\":30}";

        HttpPost request = new HttpPost(BASE_URL + "/api/analytics/stats/product/history");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("商品历史统计响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetCategorySalesStats() throws IOException, ParseException {
        HttpPost request = new HttpPost(BASE_URL + "/api/analytics/stats/category/sales");
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("分类销售统计响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}