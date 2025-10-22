package com.customers.controller;

import com.customers.controller.dto.AddressRequestDTO;
import com.customers.controller.dto.AddressResponseDTO;
import com.customers.mapper.AddressMapper;
import com.customers.model.Address;
import com.customers.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @PathVariable UUID customerId,
            @RequestBody @Valid AddressRequestDTO addressRequestDTO){

        Address address = addressService.createAddress(customerId, addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressMapper.mapToResponse(address));
    }
}
