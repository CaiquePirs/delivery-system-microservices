package com.customers.service;

import com.customers.controller.advice.exceptions.NotFoundException;
import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.mapper.AddressMapper;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.repository.CustomerRepository;
import com.customers.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerValidator validator;
    private final AddressMapper addressMapper;

    @Transactional
    public Customer createCustomer(CustomerRequestDTO dto) {
        validator.checkIfExistCustomerWithSameEmail(dto.email());
        Address address = addressMapper.mapToEntity(dto.address());

        Customer customer = Customer.builder()
                .name(dto.name())
                .phone(dto.phone())
                .email(dto.email())
                .addresses(new ArrayList<>())
                .build();

        customer.getAddresses().add(address);
        address.setCustomer(customer);
        return repository.save(customer);
    }

    public Customer findCustomerById(UUID customerId) {
       return repository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer ID not found"));
    }

    public void deleteCustomerById(UUID uuid) {
        Customer customer = findCustomerById(uuid);
        repository.delete(customer);
    }
}
