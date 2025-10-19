package com.deliverysystem.restaurants.service;

import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantNotFoundException;
import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
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
    private final RedisService redisService;

    public Restaurant createRestaurant(RestaurantRequestDTO dto){
        validator.checkIfExistRestaurantWithSameEmail(dto.email());

        Restaurant restaurant = mapper.toEntity(dto);
        return repository.save(restaurant);
    }

    public Restaurant findById(UUID restaurantId){
       return repository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        String.format("Restaurant ID: %s not found", restaurantId)));
    }
}
