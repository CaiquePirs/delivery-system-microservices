package com.deliverysystem.orders.validator;

import com.deliverysystem.orders.controller.exception.UserNotAuthorizedException;
import com.deliverysystem.orders.service.validator.AccessValidator;
import org.junit.jupiter.api.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccessValidatorTest {

    private AccessValidator accessValidator;

    @BeforeEach
    void setup() {
        accessValidator = new AccessValidator();
        SecurityContextHolder.clearContext();
    }

    private Jwt mockJwt(Map<String, Object> claims) {
        return new Jwt(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );
    }

    private void mockAuthenticationWithJwt(Jwt jwt) {
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void shouldReturnTrueWhenScopeContainsInternalService() {
        Jwt jwt = mockJwt(Map.of("scope", "internal-service other-scope"));
        mockAuthenticationWithJwt(jwt);

        assertTrue(accessValidator.isInternalService(SecurityContextHolder.getContext().getAuthentication()));
    }

//    @Test
//    void shouldReturnFalseWhenScopeIsNull() {
//        Jwt jwt = mockJwt(Map.of());
//        mockAuthenticationWithJwt(jwt);
//
//        assertFalse(accessValidator.isInternalService(SecurityContextHolder.getContext().getAuthentication()));
//    }

    @Test
    void shouldReturnFalseWhenAuthenticationIsNotJwt() {
        Authentication nonJwtAuth = mock(Authentication.class);

        assertFalse(accessValidator.isInternalService(nonJwtAuth));
    }

    @Test
    void shouldReturnCustomerIdWhenPresent() {
        UUID expectedId = UUID.randomUUID();
        Jwt jwt = mockJwt(Map.of("customer_id", expectedId.toString()));
        mockAuthenticationWithJwt(jwt);

        UUID result = accessValidator.getCustomerIdLogged();

        assertEquals(expectedId, result);
    }

//    @Test
//    void shouldThrowExceptionWhenCustomerIdMissing() {
//        Jwt jwt = mockJwt(Map.of());
//        mockAuthenticationWithJwt(jwt);
//
//        assertThrows(UserNotAuthorizedException.class, () -> accessValidator.getCustomerIdLogged());
//    }


    @Test
    void shouldReturnRestaurantIdWhenPresent() {
        UUID expectedId = UUID.randomUUID();
        Jwt jwt = mockJwt(Map.of("restaurant_id", expectedId.toString()));
        mockAuthenticationWithJwt(jwt);

        UUID result = accessValidator.getRestaurantIdLogged();

        assertEquals(expectedId, result);
    }

//    @Test
//    void shouldThrowExceptionWhenRestaurantIdMissing() {
//        Jwt jwt = mockJwt(Map.of());
//        mockAuthenticationWithJwt(jwt);
//
//        assertThrows(UserNotAuthorizedException.class, () -> accessValidator.getRestaurantIdLogged());
//    }

    @Test
    void shouldReturnTrueWhenRestaurantOwner() {
        UUID id = UUID.randomUUID();
        Jwt jwt = mockJwt(Map.of("restaurant_id", id.toString()));
        mockAuthenticationWithJwt(jwt);

        assertTrue(accessValidator.isRestaurantOwner(id));
    }

    @Test
    void shouldReturnFalseWhenNotRestaurantOwner() {
        UUID logged = UUID.randomUUID();
        UUID request = UUID.randomUUID();
        Jwt jwt = mockJwt(Map.of("restaurant_id", logged.toString()));
        mockAuthenticationWithJwt(jwt);

        assertFalse(accessValidator.isRestaurantOwner(request));
    }

    @Test
    void shouldReturnTrueWhenCustomerOwner() {
        UUID id = UUID.randomUUID();
        Jwt jwt = mockJwt(Map.of("customer_id", id.toString()));
        mockAuthenticationWithJwt(jwt);

        assertTrue(accessValidator.isCustomerOwner(id));
    }

    @Test
    void shouldReturnFalseWhenNotCustomerOwner() {
        UUID logged = UUID.randomUUID();
        UUID request = UUID.randomUUID();
        Jwt jwt = mockJwt(Map.of("customer_id", logged.toString()));
        mockAuthenticationWithJwt(jwt);

        assertFalse(accessValidator.isCustomerOwner(request));
    }

}
