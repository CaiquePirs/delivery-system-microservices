package com.deliverysystem.orders.mapper;

import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderResponseDTO;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.model.enums.AuditStatus;
import com.deliverysystem.orders.model.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderMapper {

    public Order mapToEntity(OrderRequestDTO orderDTO, List<ItemsOrder> items, BigDecimal totalOrder){
       return Order.builder()
                .notes(orderDTO.notes())
                .paymentData(orderDTO.paymentData())
                .status(OrderStatus.PENDING_PAYMENT)
                .orderDate(LocalDate.now())
                .itemsOrder(items)
                .total(totalOrder)
                .restaurantId(orderDTO.restaurantId())
                .customerId(orderDTO.customerId())
                .estimated_delivery(LocalDateTime.now().plusHours(2))
                .auditStatus(AuditStatus.ACTIVE)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
    }

    public OrderResponseDTO mapToResponse(Order order){
        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .restaurantId(order.getRestaurantId())
                .items(order.getItemsOrder())
                .status(order.getStatus())
                .total(order.getTotal())
                .orderDate(order.getOrderDate())
                .estimated_delivery(order.getEstimated_delivery())
                .notes(order.getNotes())
                .customerId(order.getCustomerId())
                .build();
    }
}
