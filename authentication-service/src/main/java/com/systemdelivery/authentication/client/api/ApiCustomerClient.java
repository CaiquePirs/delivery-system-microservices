package com.systemdelivery.authentication.client.api;

import com.systemdelivery.authentication.controller.dto.CreateCustomerRequestDTO;
import com.systemdelivery.authentication.controller.dto.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "customers-service")
public interface ApiCustomerClient {

    @PostMapping("/api/customers")
    ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CreateCustomerRequestDTO customerRequestDTO);

    @DeleteMapping("/api/{id}")
    ResponseEntity<Void> deleteCustomer(@PathVariable(name = "id") UUID customerId);
}
