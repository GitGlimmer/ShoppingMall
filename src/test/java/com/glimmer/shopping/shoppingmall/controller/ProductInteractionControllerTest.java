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
public class ProductInteractionControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testRecordInteraction() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\",\"userId\":\"test-user-001\",\"interactionType\":\"VIEW\",\"sessionId\":\"session-123\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/interaction");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("记录互动响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetProductInteractionStats() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/interaction/product/stats");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取互动统计响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testGetInteractionCount() throws IOException, ParseException {
        String requestJson = "{\"id\":\"prod-001\",\"interactionType\":\"VIEW\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/interaction/count");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("获取互动次数响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}