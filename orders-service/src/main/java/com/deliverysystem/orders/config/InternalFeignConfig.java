package com.deliverysystem.orders.config;

import com.deliverysystem.orders.client.service.TokenClientService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InternalFeignConfig {

    private final TokenClientService tokenClientService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (requestTemplate ->  {
            String token = tokenClientService.getAccessToken();
            requestTemplate.header("Authorization", "Bearer " + token);
        });
    }
}
