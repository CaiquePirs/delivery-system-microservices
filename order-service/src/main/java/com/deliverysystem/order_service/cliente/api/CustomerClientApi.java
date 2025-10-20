package com.deliverysystem.order_service.cliente.api;

import com.deliverysystem.order_service.cliente.representation.CustomerRepresentationDTO;
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

}
