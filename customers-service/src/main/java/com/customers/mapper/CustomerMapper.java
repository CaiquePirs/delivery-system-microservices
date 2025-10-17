package com.customers.mapper;

import com.customers.controller.dto.AddressResponseDTO;
import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.model.Address;
import com.customers.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {

    public CustomerResponseDTO mapToResponse(Customer customer){
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .addressResponse(mapCustomerAddress(customer.getAddresses()))
                .build();
    }

    private List<AddressResponseDTO>mapCustomerAddress(List<Address> addresses){
        return addresses.stream()
                .map(a -> AddressResponseDTO.builder()
                        .id(a.getId())
                        .state(a.getState())
                        .city(a.getCity())
                        .zipcode(a.getZipcode())
                        .neighborhood(a.getNeighborhood())
                        .number(a.getNumber())
                        .street(a.getStreet())
                        .country(a.getCountry())
                        .build()
                ).toList();
    }


}
