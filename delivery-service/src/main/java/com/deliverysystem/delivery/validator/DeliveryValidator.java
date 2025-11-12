package com.deliverysystem.delivery.validator;

import com.deliverysystem.delivery.controller.advice.exceptions.RestaurantNotAuthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeliveryValidator {

    public void validateAuthenticatedRestaurantOwnership(UUID deliveryRestaurantId){
        if(deliveryRestaurantId == null || !isRestaurantOwner(deliveryRestaurantId, getJwtAuthentication())){
            throw new RestaurantNotAuthorizedException("Restaurant not authorized to perform this request");
        }
    }

    private Jwt getJwtAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken authenticationToken){
            return authenticationToken.getToken();
        }
        throw new RestaurantNotAuthorizedException("Invalid token for the logged-in restaurant");
    }

    private UUID getRestaurantIdLogged(Jwt authJwt) {
        String restaurantId = authJwt.getClaimAsString("restaurant_id");
        return restaurantId != null ? UUID.fromString(restaurantId) : null;
    }

    private boolean isRestaurantOwner(UUID requestRestaurantId, Jwt jwt) {
        UUID currentRestaurantId = getRestaurantIdLogged(jwt);
        if(currentRestaurantId == null){
            throw new RestaurantNotAuthorizedException("Restaurant not authorized to perform this request ");
        }
        return currentRestaurantId.equals(requestRestaurantId);
    }
}
