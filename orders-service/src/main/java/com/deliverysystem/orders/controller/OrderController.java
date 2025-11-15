package com.deliverysystem.orders.controller;

import com.deliverysystem.orders.controller.dto.OrderHistoryResponseDTO;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderResponseDTO;
import com.deliverysystem.orders.service.OrderService;
import com.deliverysystem.orders.service.validator.AccessValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> createOrder(@RequestBody @Valid OrderRequestDTO dto){
        orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@accessValidator.isInternalService(authentication)")
    public ResponseEntity<OrderResponseDTO> findOrderById(@PathVariable(name = "id") String orderId){
        OrderResponseDTO orderResponse = orderService.findOrderResponseById(orderId);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/{id}/customers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderHistoryResponseDTO>> findAllOrdersByCustomerId(
            @PathVariable(name = "id") UUID customerId,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {

        Page<OrderHistoryResponseDTO> ordersPage = orderService.findAllOrdersByCustomerID(
                customerId, PageRequest.of(page, size)
        );

        return ResponseEntity.ok(ordersPage);
    }

    @GetMapping("/{id}/restaurants")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<Page<OrderHistoryResponseDTO>> findAllOrdersByRestaurantId(
            @PathVariable(name = "id") UUID restaurantId,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {

        Page<OrderHistoryResponseDTO> ordersPage = orderService.findAllOrdersByRestaurantID(
                restaurantId, PageRequest.of(page, size)
        );

        return ResponseEntity.ok(ordersPage);
    }

}
