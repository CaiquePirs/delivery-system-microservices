package com.deliveysistem.notification.config;

import com.deliveysistem.notification.client.service.TokenClientService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${KEYCLOAK_JWK_URI}")
    private String JWK_URI;

    private final TokenClientService tokenClientService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwkSetUri(JWK_URI)))
                .build();
    }

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            String token = tokenClientService.getAccessToken();
            if(token != null) {
                log.error("It was not possible to process the request and obtain the access token");
            }
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }
}
