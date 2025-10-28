package com.deliverysystem.orders.client.api;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "customers-service")
public interface CustomerClientApi {

    @GetMapping("/api/customers/{id}")
    ResponseEntity<CustomerRepresentationDTO> findCustomerById(@PathVariable(name = "id" ) UUID customerId);

    @GetMapping("/my-addresses/{id}")
    ResponseEntity<AddressRepresentationDTO> findAddressById(@PathVariable("id") UUID addressId);

}