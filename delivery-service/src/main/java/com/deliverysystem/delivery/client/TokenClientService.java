package com.deliverysystem.delivery.client;

import com.deliverysystem.delivery.client.representation.AccessTokenRequest;
import com.deliverysystem.delivery.client.representation.AccessTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    public String getAccessToken() {
        try {
            var tokenResponse = restTemplate.postForEntity(
                    "http://localhost:8080/api/auth/internal-login",
                    new AccessTokenRequest(CLIENT_ID, CLIENT_SECRET),
                    AccessTokenResponse.class
            );

            return tokenResponse.getBody() != null ? tokenResponse.getBody().accessToken() : null;

        } catch (Exception e) {
            log.error("It was not possible to process the request and obtain the access token: {}", e.getMessage());
            return null;
        }
    }
}
