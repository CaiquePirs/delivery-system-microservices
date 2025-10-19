package com.deliverysystem.restaurants.service;

import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantNotFoundException;
import com.deliverysystem.restaurants.controller.dto.RestaurantQueryFilter;
import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.enums.RestaurantStatus;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.repository.RestaurantSpecification;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Page<RestaurantResponseDTO> findRestaurantsByFilter(RestaurantQueryFilter filter, Pageable pageable){
        Page<Restaurant> restaurantsPage = repository.findAll(
                RestaurantSpecification.specification(filter), pageable);

        List<RestaurantResponseDTO> restaurantList = restaurantsPage.map(mapper::toResponse).toList();

        return new PageImpl<>(restaurantList, pageable, restaurantList.size());
    }
}
