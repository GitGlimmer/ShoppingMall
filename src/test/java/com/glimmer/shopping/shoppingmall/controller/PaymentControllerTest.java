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
public class PaymentControllerTest {

    private static final String BASE_URL = "http://localhost:8081";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void testCreatePayment() throws IOException, ParseException {
        String randomId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String paymentJson = String.format(
            "{\"id\":\"%s\",\"orderNo\":\"ORDER-%s\",\"amount\":\"99.99\",\"paymentMethod\":\"ALIPAY\",\"status\":\"PENDING\"}",
            randomId, randomId
        );

        HttpPost request = new HttpPost(BASE_URL + "/api/payment");
        request.setEntity(new StringEntity(paymentJson, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("创建支付响应: " + responseBody);

            assertNotNull(responseBody);
        }
    }
}