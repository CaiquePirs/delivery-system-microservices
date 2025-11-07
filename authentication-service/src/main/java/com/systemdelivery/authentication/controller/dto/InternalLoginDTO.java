package com.systemdelivery.authentication.controller.dto;

public record InternalLoginDTO(
        String clientId,
        String  clientSecret) {
}
