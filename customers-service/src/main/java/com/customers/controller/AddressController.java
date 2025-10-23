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
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody @Valid AddressRequestDTO addressRequestDTO){
        Address address = addressService.createAddress(addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressMapper.mapToResponse(address));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> findAddressById(@PathVariable("id") UUID addressId){
        Address address = addressService.findById(addressId);
        return ResponseEntity.ok(addressMapper.mapToResponse(address));
    }
}
