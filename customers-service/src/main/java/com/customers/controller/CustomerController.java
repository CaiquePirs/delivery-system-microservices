package com.customers.controller;

import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.mapper.CustomerMapper;
import com.customers.model.Customer;
import com.customers.service.CustomerService;
import com.customers.service.RedisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final RedisService redisService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody @Valid CustomerRequestDTO dto){
        Customer customer = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerMapper.mapToResponse(customer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findCustomerById(@PathVariable(name = "id") UUID customerId){
        CustomerResponseDTO cachedCustomer = redisService.findCustomerInCache(customerId);

        if(cachedCustomer != null){
            return ResponseEntity.ok(cachedCustomer);
        }

        Customer customer = customerService.findCustomerById(customerId);
        redisService.insertCustomerInCache(customer);
        return ResponseEntity.ok(customerMapper.mapToResponse(customer));
    }
}
