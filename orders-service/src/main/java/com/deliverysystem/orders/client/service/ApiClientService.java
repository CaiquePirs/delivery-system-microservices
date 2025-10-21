package com.deliverysystem.orders.client.service;

import com.deliverysystem.orders.client.api.CustomerClientApi;
import com.deliverysystem.orders.client.api.MenuClientApi;
import com.deliverysystem.orders.client.api.RestaurantClientApi;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import com.deliverysystem.orders.client.representation.MenuRepresentationDTO;
import com.deliverysystem.orders.client.representation.RestaurantRepresentationDTO;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiClientService {

    private final CustomerClientApi customerClientApi;
    private final RestaurantClientApi restaurantClientApi;
    private final MenuClientApi menuClientApi;

    public CustomerRepresentationDTO findCustomerById(UUID customerId) {
        try {
            var customerResponse = customerClientApi.findCustomerById(customerId);
            return customerResponse.getBody();

        } catch (FeignException e) {
            throw new ClientNotFoundException("Customer not found. Please enter the ID correctly.");
        }
    }

    public RestaurantRepresentationDTO findRestaurantById(UUID restaurantId){
        try {
            var restaurantResponse = restaurantClientApi.findRestaurantById(restaurantId);
            return restaurantResponse.getBody();

        } catch (FeignException e) {
            throw new ClientNotFoundException("Restaurant not found. Please enter the ID correctly.");
        }
    }


    public MenuRepresentationDTO findMenuById(UUID menuId, UUID restaurantId){
        try {
            var menuResponse = menuClientApi.findMenuById(restaurantId, menuId);
            return menuResponse.getBody();

        } catch (FeignException e){
            throw new ClientNotFoundException("Menu not found. Please enter the ID correctly.");
        }
    }

}
