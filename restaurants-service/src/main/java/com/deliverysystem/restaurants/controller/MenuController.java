package com.deliverysystem.restaurants.controller;

import com.deliverysystem.restaurants.controller.dto.MenuRequestDTO;
import com.deliverysystem.restaurants.controller.dto.MenuResponseDTO;
import com.deliverysystem.restaurants.mapper.MenuMapper;
import com.deliverysystem.restaurants.model.Menu;
import com.deliverysystem.restaurants.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;

    @PostMapping
    public ResponseEntity<MenuResponseDTO> createMenu(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid MenuRequestDTO menuRequestDTO){

        Menu menu = menuService.createMenu(restaurantId, menuRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuMapper.toResponse(menu));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> findMenuById(
            @PathVariable UUID restaurantId,
            @PathVariable(name = "id") UUID menuId){

        Menu menu = menuService.findMenuById(restaurantId, menuId);
        return ResponseEntity.ok(menuMapper.toResponse(menu));
    }
}
