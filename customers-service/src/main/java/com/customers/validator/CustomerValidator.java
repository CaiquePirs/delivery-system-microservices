package com.customers.validator;

import com.customers.controller.advice.exceptions.CustomerFoundException;
import com.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private final CustomerRepository customerRepository;

    public void checkIfExistCustomerWithSameEmail(String email) {
        customerRepository.findByEmail(email).ifPresent(customer -> {
            throw new CustomerFoundException(
                    String.format("Customer with email: %s already exist", email));
        });
    }

}
