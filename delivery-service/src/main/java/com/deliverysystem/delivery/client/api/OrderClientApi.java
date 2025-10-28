package com.deliverysystem.delivery.client.api;

import com.deliverysystem.delivery.client.representation.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "orders-service")
public interface OrderClientApi {

    @GetMapping("/api/orders/{id}")
    ResponseEntity<OrderDTO> findById(@PathVariable(name = "id") String orderId);
}
