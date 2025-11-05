//package com.deliverysystem.restaurants.controller;
//
//import com.deliverysystem.restaurants.service.RestaurantService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
////@RestController
////@RequestMapping("/api/restaurants/profile")
////@RequiredArgsConstructor
//public class RestaurantProfileController {
//
//    private final RestaurantService restaurantService;
//
//    @PatchMapping("/{id}")
//    @PreAuthorize("hasRole('RESTAURANT')")
//    public ResponseEntity<Void> toggleRestaurantStatus(@PathVariable(name = "id") UUID restaurantId) {
//        restaurantService.toggleRestaurantStatus(restaurantId);
//        return ResponseEntity.noContent().build();
//    }
//
//}
