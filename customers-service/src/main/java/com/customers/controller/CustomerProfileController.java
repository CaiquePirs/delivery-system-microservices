package com.customers.controller;

import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.mapper.CustomerMapper;
import com.customers.model.Customer;
import com.customers.service.CustomerService;
import com.customers.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customers/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseDTO> getMyProfile(@AuthenticationPrincipal Jwt auth) {
        Customer customer = customerValidator.resolverAndFindCustomerLogged(auth);
        return ResponseEntity.ok(customerMapper.mapToResponse(customer));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> disableMyProfile(@AuthenticationPrincipal Jwt auth) {
        Customer customer = customerValidator.resolverAndFindCustomerLogged(auth);
        customerService.disableCustomerById(customer.getId());
        return ResponseEntity.noContent().build();
    }
}
