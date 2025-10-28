package com.deliverysystem.orders.client.api;

import com.deliverysystem.orders.client.representation.MenuRepresentationDTO;
import com.deliverysystem.orders.client.representation.RestaurantRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "restaurants-service")
public interface RestaurantClientApi {

    @GetMapping("/api/restaurants/{id}")
    ResponseEntity<RestaurantRepresentationDTO> findRestaurantById(@PathVariable(name = "id" ) UUID restaurantId);

    @GetMapping("/api/restaurants/{restaurantId}/menus/{id}")
    ResponseEntity<MenuRepresentationDTO> findMenuById(@PathVariable UUID restaurantId, @PathVariable(name = "id") UUID menuId);
}
