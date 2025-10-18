package com.customers.service;

import com.customers.controller.advice.exceptions.CustomerNotFoundException;
import com.customers.controller.dto.AddressRequestDTO;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.repository.AddressRepository;
import com.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public Address createAddress(AddressRequestDTO dto){
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer ID not found"));

        Address address = Address.builder()
                .zipcode(dto.zipcode())
                .street(dto.street())
                .country(dto.country())
                .state(dto.state())
                .number(dto.number())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .build();

        customer.getAddresses().add(address);
        return addressRepository.save(address);
    }

    public List<Address> createAddressByList(List<AddressRequestDTO> requests){
        return requests.stream().map(dto -> Address.builder()
                        .street(dto.street())
                        .zipcode(dto.zipcode())
                        .city(dto.city())
                        .number(dto.number())
                        .neighborhood(dto.neighborhood())
                        .state(dto.state())
                        .country(dto.country())
                        .build()
        ).toList();
    }


}
