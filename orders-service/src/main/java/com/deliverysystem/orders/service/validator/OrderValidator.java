package com.deliverysystem.orders.service.validator;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import com.deliverysystem.orders.controller.exception.RestaurantClosedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final ApiClientService apiClientService;

    public AddressRepresentationDTO resolveDeliveryAddress(UUID deliveryAddressId, CustomerRepresentationDTO customer){
       return customer.address().stream()
                .filter(address -> address.id().equals(deliveryAddressId))
                .findFirst()
                .orElseGet(() -> apiClientService.findAddressById(deliveryAddressId));
    }

    public void validateIfRestaurantIsOpen(String status){
        if(status.equals("CLOSED")){
            throw new RestaurantClosedException("The selected restaurant is currently closed for orders.");
        }
    }

}
