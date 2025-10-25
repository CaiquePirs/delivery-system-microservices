package com.deliveysistem.notification.client.api;

import com.deliveysistem.notification.event.representation.OrderEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "OrdersApi", url = "${spring.clients.orders-api}")
public interface OrdersClientApi {

    @GetMapping("/{id}")
    ResponseEntity<OrderEvent> findOrderById(@PathVariable("id") String orderId);
}
