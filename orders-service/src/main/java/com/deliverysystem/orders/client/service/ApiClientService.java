package com.deliverysystem.orders.client.service;

import com.deliverysystem.orders.client.api.CustomerClientApi;
import com.deliverysystem.orders.client.api.RestaurantClientApi;
import com.deliverysystem.orders.client.representation.*;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiClientService {

    private final CustomerClientApi customerClientApi;
    private final RestaurantClientApi restaurantClientApi;

    @Async
    public CompletableFuture<CustomerDTO> findCustomerById(UUID customerId) {
        try {
            var customerResponse = customerClientApi.findCustomerById(customerId);
            return CompletableFuture.completedFuture(customerResponse.getBody());

        } catch (FeignException e) {
            throw new ClientNotFoundException(String.format("Customer ID: %s not found", customerId));
        }
    }

    @Async
    public CompletableFuture<RestaurantDTO> findRestaurantById(UUID restaurantId) {
        try {
            var restaurantResponse = restaurantClientApi.findRestaurantById(restaurantId);
            return CompletableFuture.completedFuture(restaurantResponse.getBody());

        } catch (FeignException e) {
            throw new ClientNotFoundException(String.format("Restaurant ID: %s not found", restaurantId));
        }
    }

    public MenuDTO findMenuById(UUID menuId, UUID restaurantId){
        try {
            var menuResponse = restaurantClientApi.findMenuById(restaurantId, menuId);
            return menuResponse.getBody();

        } catch (FeignException e){
            throw new ClientNotFoundException(String.format("Menu ID: %s not found", menuId));
        }
    }

    public DeliveryAddressDTO findAddressById(UUID addressId) {
        try {
            var address = customerClientApi.findAddressById(addressId);
            return address.getBody();

        } catch (FeignException e) {
            throw new ClientNotFoundException(String.format("Address ID: %s not found", addressId));
        }
    }
}
