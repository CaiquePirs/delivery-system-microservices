package com.deliverysystem.restaurants.controller.dto;

import com.deliverysystem.restaurants.model.enums.RestaurantStatus;
import com.deliverysystem.restaurants.model.enums.MenuType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class RestaurantQueryFilter{

    private RestaurantStatus restaurantStatus;
    private String restaurantName;
    private MenuType menuType;
    private BigDecimal menuPrice;
}
