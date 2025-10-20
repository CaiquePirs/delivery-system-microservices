package com.deliverysystem.order_service.cliente.api;

import com.deliverysystem.order_service.cliente.representation.MenuRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "MenusApi", url = "${restaurants.url}")
public interface MenuClientApi {

    @GetMapping("/{restaurantId}/menus/{id}")
    ResponseEntity<MenuRepresentationDTO> findMenuById(
            @PathVariable UUID restaurantId,
            @PathVariable(name = "id") UUID menuId);
}
