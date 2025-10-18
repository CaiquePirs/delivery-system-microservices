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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody @Valid AddressRequestDTO addressRequestDTO){
        Address address = addressService.createAddress(addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressMapper.mapToResponse(address));
    }
}
