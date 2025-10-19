package com.deliverysystem.restaurants.service;

import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantNotFoundException;
import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.RestaurantStatus;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository repository;
    private final RestaurantValidator validator;
    private final RestaurantMapper mapper;

    public Restaurant createRestaurant(RestaurantRequestDTO dto){
        validator.checkIfExistRestaurantWithSameEmail(dto.email());

        Restaurant restaurant = mapper.toEntity(dto);
        restaurant.setStatus(RestaurantStatus.OPEN);
        return repository.save(restaurant);
    }

    public Restaurant findById(UUID restaurantId){
       return repository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        String.format("Restaurant ID: %s not found", restaurantId)));
    }

    public void toggleRestaurantStatus(UUID restaurantId){
        Restaurant restaurant = findById(restaurantId);

        if(restaurant.getStatus().equals(RestaurantStatus.OPEN)){
            restaurant.setStatus(RestaurantStatus.CLOSED);
        } else {
            restaurant.setStatus(RestaurantStatus.OPEN);
        }

        repository.save(restaurant);
    }
}
