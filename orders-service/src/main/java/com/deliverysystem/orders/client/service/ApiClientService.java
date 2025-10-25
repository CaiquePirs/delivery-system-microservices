package com.deliverysystem.orders.client.service;

import com.deliverysystem.orders.client.api.AddressClientApi;
import com.deliverysystem.orders.client.api.CustomerClientApi;
import com.deliverysystem.orders.client.api.MenuClientApi;
import com.deliverysystem.orders.client.api.RestaurantClientApi;
import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import com.deliverysystem.orders.client.representation.MenuRepresentationDTO;
import com.deliverysystem.orders.client.representation.RestaurantRepresentationDTO;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ApiClientService {

    private final CustomerClientApi customerClientApi;
    private final RestaurantClientApi restaurantClientApi;
    private final AddressClientApi addressClientApi;
    private final MenuClientApi menuClientApi;

    @Async
    public CompletableFuture<CustomerRepresentationDTO> findCustomerById(UUID customerId) {
        var customerResponse = customerClientApi.findCustomerById(customerId);
        return CompletableFuture.completedFuture(customerResponse.getBody());
    }

    @Async
    public CompletableFuture<RestaurantRepresentationDTO> findRestaurantById(UUID restaurantId) {
        var restaurantResponse = restaurantClientApi.findRestaurantById(restaurantId);
        return CompletableFuture.completedFuture(restaurantResponse.getBody());
    }

    public MenuRepresentationDTO findMenuById(UUID menuId, UUID restaurantId){
        try {
            var menuResponse = menuClientApi.findMenuById(restaurantId, menuId);
            return menuResponse.getBody();

        } catch (FeignException e){
            throw new ClientNotFoundException("Menu not found. Please enter the ID correctly.");
        }
    }

    public AddressRepresentationDTO findAddressById(UUID addressId) {
        try {
            var address = addressClientApi.findAddressById(addressId);
            return address.getBody();

        } catch (Exception e) {
            throw new ClientNotFoundException("Address not found. Please enter the ID correctly.");
        }
    }

}
