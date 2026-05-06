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
public class ProductRatingControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testAddOrUpdateRating() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\",\"userId\":\"test-user-001\",\"rating\":4.5,\"comment\":\"商品不错，推荐购买\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/rating");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("添加评分响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetAverageRating() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/rating/product/average");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取平均评分响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetRatingStatistics() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/rating/product/stats");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取评分统计响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}