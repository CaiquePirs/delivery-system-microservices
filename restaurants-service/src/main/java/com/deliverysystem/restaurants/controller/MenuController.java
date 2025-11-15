package com.deliverysystem.restaurants.controller;

import com.deliverysystem.restaurants.controller.dto.MenuRequestDTO;
import com.deliverysystem.restaurants.controller.dto.MenuResponseDTO;
import com.deliverysystem.restaurants.mapper.MenuMapper;
import com.deliverysystem.restaurants.model.Menu;
import com.deliverysystem.restaurants.service.MenuService;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;
    private final RestaurantValidator restaurantValidator;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<MenuResponseDTO> createMenu(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable UUID restaurantId,
            @RequestBody @Valid MenuRequestDTO menuRequestDTO) {

        restaurantValidator.validateIfIsSameRestaurant(restaurantId, auth);
        Menu menu = menuService.createMenu(restaurantId, menuRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuMapper.toResponse(menu));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<MenuResponseDTO> findAvailableMenuById(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable UUID restaurantId,
            @PathVariable(name = "id") UUID menuId) {

        restaurantValidator.validateIfIsRestaurantOrInternalService(restaurantId, auth);
        Menu menu = menuService.findAvailableMenuById(restaurantId, menuId);
        return ResponseEntity.ok(menuMapper.toResponse(menu));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<Void> disableMenuById(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable UUID restaurantId,
            @PathVariable(name = "id") UUID menuId){

        restaurantValidator.validateIfIsSameRestaurant(restaurantId, auth);
        menuService.disableMenuById(menuId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<Void> toggleMenuStatus(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable UUID restaurantId,
            @PathVariable(name = "id") UUID menuId) {

        restaurantValidator.validateIfIsSameRestaurant(restaurantId, auth);
        menuService.toggleMenuStatus(menuId);
        return ResponseEntity.noContent().build();
    }
}
