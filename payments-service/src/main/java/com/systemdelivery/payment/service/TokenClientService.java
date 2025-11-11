package com.systemdelivery.payment.service;

import com.systemdelivery.payment.controller.dto.AccessTokenRequest;
import com.systemdelivery.payment.controller.dto.AccessTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @Value("${SERVICE_TOKEN_URL}")
    private String SERVICE_TOKEN_URL;

    public String getAccessToken() {
        try {
            var tokenResponse = restTemplate.postForEntity(
                    SERVICE_TOKEN_URL,
                    new AccessTokenRequest(CLIENT_ID, CLIENT_SECRET),
                    AccessTokenResponse.class
            );
            return tokenResponse.getBody() != null ? tokenResponse.getBody().accessToken() : null;

        } catch (Exception e) {
            log.error("It was not possible to process the request and obtain the access token: {}", e.getMessage());
            return null;
        }
    }

    public boolean isInternalService(Authentication authentication) {
        if(authentication instanceof JwtAuthenticationToken authenticationToken){
            Jwt token = authenticationToken.getToken();
            String scope = token.getClaimAsString("scope");
            return scope != null && scope.contains("internal-service");
        }
        return false;
    }

}
