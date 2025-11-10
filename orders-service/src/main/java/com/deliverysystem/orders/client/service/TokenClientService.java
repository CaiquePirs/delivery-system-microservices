package com.deliverysystem.orders.client.service;

import com.deliverysystem.orders.client.representation.AccessTokenRequest;
import com.deliverysystem.orders.client.representation.AccessTokenResponse;
import com.deliverysystem.orders.controller.exception.OrderProcessingFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TokenClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String CLIENT_ID;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String CLIENT_SECRET;

    @Value("${SERVICE_TOKEN_URL}")
    private String SERVICE_TOKEN_URL;

    public String getAccessToken() {
        var tokenResponse = restTemplate.postForEntity(
                SERVICE_TOKEN_URL,
                new AccessTokenRequest(CLIENT_ID, CLIENT_SECRET),
                AccessTokenResponse.class
        );

        if (tokenResponse.getBody() != null) {
            return tokenResponse.getBody().accessToken();

        } else {
            throw new OrderProcessingFailure("Error processing the request. Invalid token");
        }
    }
}
