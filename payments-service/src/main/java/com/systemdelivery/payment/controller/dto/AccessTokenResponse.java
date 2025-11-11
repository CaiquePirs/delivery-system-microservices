package com.systemdelivery.payment.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("token_type") String tokenType) {
}
