package com.deliverysystem.restaurants.service;

import com.deliverysystem.restaurants.controller.advice.exceptions.MenuNotFoundException;
import com.deliverysystem.restaurants.controller.dto.MenuRequestDTO;
import com.deliverysystem.restaurants.mapper.MenuMapper;
import com.deliverysystem.restaurants.model.Menu;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.enums.MenuStatus;
import com.deliverysystem.restaurants.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantService restaurantService;
    private final MenuMapper menuMapper;

    public Menu createMenu(UUID restaurantId, MenuRequestDTO dto){
        Restaurant restaurant = restaurantService.findById(restaurantId);

        Menu menu = menuMapper.toEntity(dto);
        menu.setStatus(MenuStatus.AVAILABLE);
        menu.setRestaurant(restaurant);

        restaurant.getMenus().add(menu);
        return menuRepository.save(menu);
    }

    public Menu findMenuById(UUID restaurantId, UUID menuId){
        Menu menu = menuRepository.findById(menuId)
                .filter(m -> !m.getStatus().equals(MenuStatus.UNAVAILABLE))
                .orElseThrow(() -> new MenuNotFoundException(String.format("Menu ID: %s not found", menuId)));

        Restaurant restaurant = restaurantService.findById(restaurantId);

        restaurant.getMenus()
                .stream()
                .filter(m -> m.getId().equals(menu.getId()))
                .findFirst()
                .orElseThrow(() -> new MenuNotFoundException("This menu does not belong to this restaurant."));

        return menu;
    }

}
