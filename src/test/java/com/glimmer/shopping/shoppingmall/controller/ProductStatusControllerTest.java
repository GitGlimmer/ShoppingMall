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
public class ProductStatusControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testUpdateProductStatus() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\",\"status\":\"OFF_SALE\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/status");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("更新商品状态响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testSetRecommendedStatus() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\",\"isRecommended\":true}";

        HttpPut request = new HttpPut(BASE_URL + "/api/product/status/recommended");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("设置推荐状态响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testSetNewStatus() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\",\"isNew\":true}";

        HttpPut request = new HttpPut(BASE_URL + "/api/product/status/new");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("设置新品状态响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testSetHotStatus() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\",\"isHot\":true}";

        HttpPut request = new HttpPut(BASE_URL + "/api/product/status/hot");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("设置热销状态响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testUpdateSortWeight() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\",\"limit\":100}";

        HttpPut request = new HttpPut(BASE_URL + "/api/product/status/sort");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("更新排序权重响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}