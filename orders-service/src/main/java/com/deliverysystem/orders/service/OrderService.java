package com.deliverysystem.orders.service;

import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import com.deliverysystem.orders.controller.exception.OrderNotFoundException;
import com.deliverysystem.orders.mapper.OrderMapper;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.repository.OrderRepository;
import com.deliverysystem.orders.service.calculator.OrderCalculator;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ApiClientService apiClientService;
    private final ItemOrderService itemOrderService;
    private final OrderCalculator calculator;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public Order createOrder(OrderRequestDTO orderDTO){
        var customer = apiClientService.findCustomerById(orderDTO.customerId());
        var restaurant = apiClientService.findRestaurantById(orderDTO.restaurantId());

        if(restaurant.status().equals("CLOSED")){
            throw new ClientNotFoundException("The selected restaurant is currently closed for orders. ");
        }

        List<ItemsOrder> items = itemOrderService.createItemsOrder(restaurant, orderDTO.itemsDTO());
        BigDecimal totalOrder = calculator.calculateTotalOrder(items);

        Order orderMapped = orderMapper.mapToEntity(orderDTO, items, totalOrder);
        return orderRepository.save(orderMapped);
    }

    public Order findById(String orderId) {
        return orderRepository.findById(new ObjectId(orderId))
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order ID: %s not found", orderId)));
    }
}
