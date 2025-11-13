package com.deliverysystem.orders.service.validator;

import com.deliverysystem.orders.controller.exception.OrderProcessingFailure;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import java.util.UUID;

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

    public UUID getCustomerIdLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt token = jwtAuth.getToken();
            String currentCustomerId = token.getClaimAsString("customer_id");

            if(currentCustomerId != null) {
                return UUID.fromString(currentCustomerId);
            }
        }
        throw new OrderProcessingFailure("Customer is not authorized for perform this request");
    }

}
