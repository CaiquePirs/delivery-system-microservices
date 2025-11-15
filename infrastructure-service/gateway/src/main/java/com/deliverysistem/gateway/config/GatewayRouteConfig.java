package com.deliverysistem.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/customers/**").uri("lb://customers-service"))
                .route(r -> r.path("/api/restaurants/**").uri("lb://restaurants-service"))
                .route(r -> r.path("/api/orders/**").uri("lb://orders-service"))
                .route(r -> r.path("/api/payments/**").uri("lb://payments-service"))
                .route(r -> r.path("/api/deliveries/**").uri("lb://deliveries-service"))
                .route(r -> r.path("/api/curriers/**").uri("lb://deliveries-service"))
                .route(r -> r.path("/api/notifications/**").uri("lb://notifications-service"))
                .route(r -> r.path("/api/auth/**").uri("lb://authentication-service"))
                .build();
    }
}
