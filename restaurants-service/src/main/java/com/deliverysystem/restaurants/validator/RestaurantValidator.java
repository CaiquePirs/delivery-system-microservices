package com.deliverysystem.restaurants.validator;

import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantFoundException;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantValidator {

    private final RestaurantRepository restaurantRepository;

    public void checkIfExistRestaurantWithSameEmail(String email){
        restaurantRepository.findByEmail(email).ifPresent(restaurant -> {
                    throw new RestaurantFoundException("This email already exit");
                });
    }

    public UUID getRestaurantIdLogged(Jwt auth) {
        String restaurantId = auth.getClaimAsString("restaurant_id");
        return restaurantId != null ? UUID.fromString(restaurantId) : null;
    }

}
