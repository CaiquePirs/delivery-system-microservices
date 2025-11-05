package com.customers.validator;

import com.customers.controller.advice.exceptions.CustomerFoundException;
import com.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private final CustomerRepository customerRepository;

    public void checkIfExistCustomerWithSameEmail(String email) {
        customerRepository.findByEmail(email).ifPresent(customer -> {
            throw new CustomerFoundException("This email already exist");
        });
    }

    public UUID getCustomerIdLogged(Jwt authJwt) {
        String customerId = authJwt.getClaimAsString("customer_id");
        return customerId != null ? UUID.fromString(customerId) : null;
    }

}
