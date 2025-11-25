package com.systemdelivery.authentication.client.api;

import com.systemdelivery.authentication.controller.dto.CreateRestaurantRequestDTO;
import com.systemdelivery.authentication.controller.dto.RestaurantResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "restaurants-service")
public interface ApiRestaurantClient {

    @PostMapping("/api/restaurants")
    ResponseEntity<RestaurantResponseDTO> createRestaurant(@RequestBody CreateRestaurantRequestDTO restaurantRequestDTO);

    @DeleteMapping("/api/restaurants/{id}")
    ResponseEntity<Void> deleteRestaurant(@PathVariable(name = "id") UUID restaurantId);
}
