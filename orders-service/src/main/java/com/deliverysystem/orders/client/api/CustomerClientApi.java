package com.deliverysystem.orders.client.api;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "CustomersApi", url = "${customers.url}")
public interface CustomerClientApi {

    @GetMapping("/{id}")
    ResponseEntity<CustomerRepresentationDTO> findCustomerById(
            @PathVariable(name = "id" ) UUID customerId);

    @GetMapping("/{customerId}/address/{id}")
    ResponseEntity<AddressRepresentationDTO> findCAddressById(
            @PathVariable(name = "id" ) UUID addressId,
            @PathVariable UUID customerId);
}