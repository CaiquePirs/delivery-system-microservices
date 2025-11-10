package com.deliverysystem.orders.client.api;

import com.deliverysystem.orders.client.representation.DeliveryAddressDTO;
import com.deliverysystem.orders.client.representation.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "customers-service")
public interface CustomerClientApi {

    @GetMapping("/api/customers/{id}")
    ResponseEntity<CustomerDTO> findCustomerById(@PathVariable(name = "id") UUID customerId);

    @GetMapping("/my-addresses/{id}")
    ResponseEntity<DeliveryAddressDTO> findAddressById(@PathVariable("id") UUID addressId);

}