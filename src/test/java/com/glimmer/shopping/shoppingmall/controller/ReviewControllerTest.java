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
public class ReviewControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testAddReview() throws IOException, ParseException {
        String randomId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String reviewJson = String.format(
            "{\"id\":\"%s\",\"productId\":\"prod-001\",\"userId\":\"test-user-001\",\"rating\":5,\"comment\":\"商品非常好用，物流也很快！\",\"createTime\":\"2024-01-01 10:00:00\"}",
            randomId
        );

        HttpPost request = new HttpPost(BASE_URL + "/api/review");
        request.setEntity(new StringEntity(reviewJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("添加评论响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetReviewsByProduct() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/review/product");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取商品评论响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}