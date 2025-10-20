package com.customers.mapper;

import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    private final AddressMapper addressMapper;

    public CustomerResponseDTO mapToResponse(Customer customer){
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(addressMapper.mapCustomerAddress(customer.getAddresses()))
                .build();
    }

}
