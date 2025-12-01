package com.deliverysystem.delivery.validator;

import com.deliverysystem.delivery.controller.advice.exceptions.RestaurantNotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DeliveryValidatorTest {

    private DeliveryValidator deliveryValidator;

    @BeforeEach
    void setup() {
        deliveryValidator = new DeliveryValidator();
        SecurityContextHolder.clearContext();
    }

    private void mockJwt(String restaurantId) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("restaurant_id")).thenReturn(restaurantId);

        Authentication auth = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldValidateOwnershipSuccessfully() {
        UUID restaurantId = UUID.randomUUID();
        mockJwt(restaurantId.toString());

        assertDoesNotThrow(() ->
                deliveryValidator.validateAuthenticatedRestaurantOwnership(restaurantId)
        );
    }

    @Test
    void shouldThrowWhenRestaurantIdDoesNotMatch() {
        UUID requestId = UUID.randomUUID();
        UUID loggedId = UUID.randomUUID();

        mockJwt(loggedId.toString());

        assertThrows(RestaurantNotAuthorizedException.class, () ->
                deliveryValidator.validateAuthenticatedRestaurantOwnership(requestId)
        );
    }

    @Test
    void shouldThrowWhenAuthenticationIsNotJwt() {
        Authentication fakeAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(fakeAuth);

        assertThrows(RestaurantNotAuthorizedException.class, () ->
                deliveryValidator.validateAuthenticatedRestaurantOwnership(UUID.randomUUID())
        );
    }
}
