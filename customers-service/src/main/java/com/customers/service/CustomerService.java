package com.customers.service;

import com.customers.controller.advice.exceptions.CustomerFoundException;
import com.customers.controller.advice.exceptions.CustomerNotFoundException;
import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressService addressService;

    public Customer createCustomer(CustomerRequestDTO dto){
        checkIfExistCustomerWithSameEmail(dto.email());

        List<Address> addresses = addressService.mapAddressToEntity(dto.addressRequest());

        Customer customer = Customer.builder()
                .name(dto.name())
                .phone(dto.phone())
                .email(dto.email())
                .addresses(addresses)
                .build();

        addresses.forEach(a -> a.setCustomer(customer));
        return customerRepository.save(customer);
    }

    private void checkIfExistCustomerWithSameEmail(String email){
        customerRepository.findByEmail(email).ifPresent(customer -> {
                    throw new CustomerFoundException(
                            String.format("Customer with email: %s already exist", email));
                });
    }

    public Customer findCustomerById(UUID customerId){
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer ID not found"));
    }

}
