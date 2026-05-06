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
public class ProductInteractionManageControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testUpdateProductRating() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\",\"rating\":4.5}";

        HttpPut request = new HttpPut(BASE_URL + "/api/product/interaction/rating");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("更新商品评分响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testIncrementViewCount() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/interaction/view");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("增加浏览次数响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }

    @Test
    public void testIncrementFavoriteCount() throws IOException, ParseException {
        String requestJson = "{\"id\":\"5706c9418e874244\"}";

        HttpPost request = new HttpPost(BASE_URL + "/api/product/interaction/favorite");
        request.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("增加收藏次数响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}