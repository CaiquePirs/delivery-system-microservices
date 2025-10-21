package com.deliverysystem.orders.service;

import com.deliverysystem.orders.client.representation.RestaurantRepresentationDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.ItemOrderRequestDTO;
import com.deliverysystem.orders.model.ItemsOrder;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemOrderService {

    private final ApiClientService apiClientService;

    public List<ItemsOrder> createItemsOrder(RestaurantRepresentationDTO restaurant, List<ItemOrderRequestDTO> itemsDTO){
        return itemsDTO.stream().map(item -> {

            var menu = restaurant.menus()
                    .stream()
                    .filter(m -> m.id().equals(item.menuId()))
                    .findFirst()
                    .orElseGet(() -> apiClientService.findMenuById(item.menuId(), restaurant.id()));

            return ItemsOrder.builder()
                    .id(new ObjectId().toString())
                    .menuId(menu.id())
                    .quantity(item.quantity())
                    .total(menu.price().multiply(BigDecimal.valueOf(item.quantity())))
                    .build();
        }).toList();
    }
}
