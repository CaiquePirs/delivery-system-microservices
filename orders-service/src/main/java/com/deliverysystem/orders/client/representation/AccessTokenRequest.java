package com.deliverysystem.orders.client.representation;

public record AccessTokenRequest(
        String clientId,
        String  clientSecret) {
}
