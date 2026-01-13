package com.deliverysystem.orders.client.service;

import com.deliverysystem.orders.client.representation.AccessTokenRequest;
import com.deliverysystem.orders.client.representation.AccessTokenResponse;
import com.deliverysystem.orders.controller.exception.UserNotAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenClientService {

    private final RestTemplate restTemplate;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @Value("${SERVICE_TOKEN_URL}")
    private String SERVICE_TOKEN_URL;

    public String getAccessToken() {
        try {
            var request = new AccessTokenRequest(CLIENT_ID, CLIENT_SECRET);
            var tokenResponse = restTemplate.postForEntity(
                    SERVICE_TOKEN_URL,
                    request,
                    AccessTokenResponse.class
            );

            if (tokenResponse.getBody() != null && tokenResponse.getStatusCode().is2xxSuccessful()) {
                return tokenResponse.getBody().accessToken();
            }
            throw new UserNotAuthorizedException("Response failed: " + tokenResponse.getStatusCode());

        } catch (Exception e) {
            throw new UserNotAuthorizedException("The service token could not be obtained. " + e.getMessage());
        }
    }
}