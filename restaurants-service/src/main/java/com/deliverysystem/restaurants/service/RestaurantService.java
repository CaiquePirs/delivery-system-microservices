package com.deliverysystem.restaurants.service;

import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository repository;
    private final RestaurantValidator validator;
    private final RestaurantMapper mapper;

    public Restaurant createRestaurant(RestaurantRequestDTO dto){
        validator.checkIfExistRestaurantWithSameEmail(dto.email());

        Restaurant restaurant = mapper.toEntity(dto);
        return repository.save(restaurant);
    }


}
