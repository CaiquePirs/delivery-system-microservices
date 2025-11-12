package com.deliverysystem.delivery.config;

import com.deliverysystem.delivery.client.TokenClientService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignConfig implements RequestInterceptor {

    private final TokenClientService tokenClientService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer " + tokenClientService.getAccessToken());
    }
}
