package com.deliverysystem.orders.controller;

import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderResponseDTO;
import com.deliverysystem.orders.mapper.OrderMapper;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderRequestDTO dto){
        Order order = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.mapToResponse(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findOrderById(@PathVariable(name = "id") String orderId){
        Order order = orderService.findById(orderId);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }
}
