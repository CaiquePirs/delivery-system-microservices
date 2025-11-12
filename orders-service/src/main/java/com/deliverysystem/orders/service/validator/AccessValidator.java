package com.deliverysystem.orders.service.validator;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

    public boolean isInternalService(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt token = jwtAuth.getToken();
            String scope = token.getClaimAsString("scope");

            return scope != null && scope.contains("internal-service");
        }
        return false;
    }
}
