package com.deliverysystem.restaurants.validator;

import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantFoundException;
import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantNotAuthorizedException;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.security.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantValidator {

    private final RestaurantRepository restaurantRepository;
    private final TokenValidator tokenValidator;

    public void checkIfExistRestaurantWithSameEmail(String email) {
        restaurantRepository.findByEmail(email).ifPresent(restaurant -> {
            throw new RestaurantFoundException("This email already exit");
        });
    }

    public UUID getRestaurantIdLogged(Jwt auth) {
        String restaurantId = auth.getClaimAsString("restaurant_id");
        return restaurantId != null ? UUID.fromString(restaurantId) : null;
    }

    public void validateIfIsSameRestaurant(UUID restaurantId, Jwt auth) {
        boolean isSameRestaurant = isSameRestaurant(restaurantId, auth);
        if (!isSameRestaurant) {
            throw new RestaurantNotAuthorizedException("User not authorized to perform this action");
        }
    }

    private boolean isSameRestaurant(UUID restaurantIdRequest, Jwt auth) {
        UUID restaurantIdLogged = getRestaurantIdLogged(auth);
        return restaurantIdLogged != null && restaurantIdLogged.equals(restaurantIdRequest);
    }

    public void validateIfIsRestaurantOrInternalService(UUID restaurantIdRequest, Jwt auth) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isInternalService = tokenValidator.isInternalService(authentication);
        if (!isInternalService && !isSameRestaurant(restaurantIdRequest, auth)) {
            throw new RestaurantNotAuthorizedException("User not authorized to perform this action");
        }
    }
}
