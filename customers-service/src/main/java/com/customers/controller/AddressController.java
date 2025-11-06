package com.customers.controller;

import com.customers.controller.dto.AddressRequestDTO;
import com.customers.controller.dto.AddressResponseDTO;
import com.customers.mapper.AddressMapper;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.service.AddressService;
import com.customers.validator.CustomerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers/my-addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final CustomerValidator customerValidator;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressResponseDTO> createAddress(@AuthenticationPrincipal Jwt auth,
                                                            @RequestBody @Valid AddressRequestDTO addressRequestDTO){

        Customer customer = customerValidator.resolverAndFindCustomerLogged(auth);
        Address address = addressService.createAddress(addressRequestDTO, customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressMapper.mapToResponse(address));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressResponseDTO> findAddressById(@AuthenticationPrincipal Jwt auth,
                                                              @PathVariable("id") UUID addressId){

        customerValidator.validateAuthenticatedCustomerOwnership(auth, addressId);
        Address address = addressService.findById(addressId);
        return ResponseEntity.ok(addressMapper.mapToResponse(address));
    }
}
