package com.deliverysystem.restaurants.controller;

import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.service.RedisService;
import com.deliverysystem.restaurants.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final RedisService redisService;

    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(
            @RequestBody @Valid RestaurantRequestDTO dto) {

        Restaurant restaurant = restaurantService.createRestaurant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantMapper.toResponse(restaurant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> findById(
            @PathVariable(name = "id") UUID restaurantId) {

        RestaurantResponseDTO cachedRestaurant = redisService.findRestaurantInCache(restaurantId);
        if (cachedRestaurant != null) {
            return ResponseEntity.ok(cachedRestaurant);
        }

        Restaurant restaurant = restaurantService.findById(restaurantId);
        redisService.insertRestaurantInCache(restaurant);

        return ResponseEntity.ok(restaurantMapper.toResponse(restaurant));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> toggleRestaurantStatus(@PathVariable(name = "id") UUID restaurantId) {
        restaurantService.toggleRestaurantStatus(restaurantId);
        return ResponseEntity.noContent().build();
    }

}
