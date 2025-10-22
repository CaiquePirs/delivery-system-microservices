package com.deliverysystem.restaurants.validator;

import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantFoundException;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantValidator {

    private final RestaurantRepository restaurantRepository;

    public void checkIfExistRestaurantWithSameEmail(String email){
        restaurantRepository.findByEmail(email).ifPresent(restaurant -> {
                    throw new RestaurantFoundException("This email already exit");
                });
    }

}
