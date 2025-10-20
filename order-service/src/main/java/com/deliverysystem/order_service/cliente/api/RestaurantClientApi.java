package com.deliverysystem.order_service.cliente.api;

import com.deliverysystem.order_service.cliente.representation.RestaurantRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "RestaurantsApi", url = "${restaurants.url}")
public interface RestaurantClientApi {

    @GetMapping("/{id}")
    ResponseEntity<RestaurantRepresentationDTO> findRestaurantById(
            @PathVariable(name = "id" ) UUID restaurantId);
}
