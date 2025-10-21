package com.deliverysystem.orders.service.validator;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final ApiClientService apiClientService;

    public AddressRepresentationDTO resolveDeliveryAddress(OrderRequestDTO orderDTO, CustomerRepresentationDTO customer){
       return customer.address().stream()
                .filter(address -> address.id().equals(orderDTO.deliveryAddressId()))
                .findFirst()
                .orElseGet(() -> apiClientService.findAddressById(customer.id(), orderDTO.deliveryAddressId()));
    }
}
