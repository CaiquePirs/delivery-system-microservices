package com.customers.service;

import com.customers.controller.advice.exceptions.CustomerNotFoundException;
import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.mapper.CustomerMapper;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.repository.CustomerRepository;
import com.customers.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final AddressService addressService;
    private final CustomerValidator validator;

    public Customer createCustomer(CustomerRequestDTO dto) {
        validator.checkIfExistCustomerWithSameEmail(dto.email());

        List<Address> addresses = addressService.createAddressByList(dto.addressRequest());

        Customer customer = Customer.builder()
                .name(dto.name())
                .phone(dto.phone())
                .email(dto.email())
                .addresses(addresses)
                .build();

        addresses.forEach(a -> a.setCustomer(customer));
        return repository.save(customer);
    }

    public Customer findCustomerById(UUID customerId) {
       return repository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Customer ID: %s not found", customerId)));
    }
}
