package com.deliverysystem.restaurants.controller;

import com.deliverysystem.restaurants.controller.dto.RestaurantQueryFilter;
import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.service.RedisService;
import com.deliverysystem.restaurants.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;
    private final RestaurantMapper restaurantMapper;
    private final RedisService redisService;

    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(
            @RequestBody @Valid RestaurantRequestDTO dto) {

        Restaurant restaurant = service.createRestaurant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantMapper.toResponse(restaurant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> findById(
            @PathVariable(name = "id") UUID restaurantId) {

        RestaurantResponseDTO cachedRestaurant = redisService.findRestaurantInCache(restaurantId);
        if (cachedRestaurant != null) {
            return ResponseEntity.ok(cachedRestaurant);
        }

        Restaurant restaurant = service.findById(restaurantId);
        redisService.insertRestaurantInCache(restaurant);

        return ResponseEntity.ok(restaurantMapper.toResponse(restaurant));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> toggleRestaurantStatus(@PathVariable(name = "id") UUID restaurantId) {
        service.toggleRestaurantStatus(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponseDTO>> findRestaurantsByQuery(
            @ModelAttribute RestaurantQueryFilter filter,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "page", defaultValue = "10") Integer size){

        Page<RestaurantResponseDTO> responseDTOPage = service.findRestaurantsByFilter(
                filter, PageRequest.of(page, size));

        return ResponseEntity.ok(responseDTOPage);
    }

}
