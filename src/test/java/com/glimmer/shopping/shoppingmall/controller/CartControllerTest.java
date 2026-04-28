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
public class CartControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testAddToCart() throws IOException, ParseException {
        String cartJson = "{\"userId\":\"test-user-001\",\"productId\":\"prod-001\",\"quantity\":2}";

        HttpPost request = new HttpPost(BASE_URL + "/api/cart");
        request.setEntity(new StringEntity(cartJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("添加购物车响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetCart() throws IOException, ParseException {
        HttpGet request = new HttpGet(BASE_URL + "/api/cart/test-user-001");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取购物车响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}