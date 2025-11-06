package com.deliverysystem.restaurants.controller;

import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.service.RestaurantService;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants/profile")
@RequiredArgsConstructor
public class RestaurantProfileController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantValidator restaurantValidator;

    @GetMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<RestaurantResponseDTO> getMyProfile(@AuthenticationPrincipal Jwt auth){
        UUID restaurantId = restaurantValidator.getRestaurantIdLogged(auth);

        if (restaurantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurantMapper.toResponse(restaurant));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<Void> disableRestaurantById(@AuthenticationPrincipal Jwt auth){
        UUID restaurantId = restaurantValidator.getRestaurantIdLogged(auth);

        if(restaurantId == null) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        restaurantService.disableRestaurantById(restaurantId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<Void> toggleRestaurantStatus(@AuthenticationPrincipal Jwt auth) {
        UUID restaurantId = restaurantValidator.getRestaurantIdLogged(auth);

        if(restaurantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        restaurantService.toggleRestaurantStatus(restaurantId);
        return ResponseEntity.noContent().build();
    }

}
