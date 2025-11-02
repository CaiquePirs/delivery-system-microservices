package com.systemdelivery.authentication.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @Value("${KEYCLOAK_SERVER_URL}")
    private String SERVER_URL;

    @Value("${KEYCLOAK_REALM}")
    private String REALM;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .build();
    }
}