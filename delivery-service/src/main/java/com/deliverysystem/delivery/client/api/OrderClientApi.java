package com.deliverysystem.delivery.client.api;

import com.deliverysystem.delivery.client.representation.OrderRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${spring.clients.order-service.url}")
public interface OrderClientApi {

    @GetMapping("/{id}")
    ResponseEntity<OrderRepresentationDTO> findById(@PathVariable(name = "id") String orderId);
}
