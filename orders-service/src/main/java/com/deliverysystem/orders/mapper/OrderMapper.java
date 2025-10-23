package com.deliverysystem.orders.mapper;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import com.deliverysystem.orders.event.representation.CustomerResponseEvent;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderResponseDTO;
import com.deliverysystem.orders.event.representation.OrderResponseEvent;
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
                .deliveryAddressId(orderDTO.deliveryAddressId())
                .estimatedDelivery(LocalDateTime.now().plusHours(2))
                .auditStatus(AuditStatus.ACTIVE)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
    }

    public OrderResponseDTO mapToResponse(Order order, CustomerRepresentationDTO customer, AddressRepresentationDTO deliveryAddress){
        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .restaurantId(order.getRestaurantId())
                .items(order.getItemsOrder())
                .status(order.getStatus())
                .total(order.getTotal())
                .orderDate(order.getOrderDate())
                .estimated_delivery(order.getEstimatedDelivery())
                .notes(order.getNotes())
                .customer(mapToCustomerResponse(customer, deliveryAddress))
                .build();
    }

    public OrderResponseEvent mapToEventResponse(Order order, CustomerRepresentationDTO customer, AddressRepresentationDTO deliveryAddress){
        return OrderResponseEvent.builder()
                .id(order.getId().toString())
                .restaurantId(order.getRestaurantId())
                .items(order.getItemsOrder())
                .status(order.getStatus())
                .total(order.getTotal())
                .orderDate(order.getOrderDate())
                .estimated_delivery(order.getEstimatedDelivery())
                .notes(order.getNotes())
                .customer(mapToCustomerResponse(customer, deliveryAddress))
                .paymentData(order.getPaymentData())
                .build();
    }

    private CustomerResponseEvent mapToCustomerResponse(CustomerRepresentationDTO customer, AddressRepresentationDTO deliveryAddress){
        return CustomerResponseEvent.builder()
                .id(customer.id())
                .name(customer.name())
                .phone(customer.phone())
                .email(customer.email())
                .deliveryAddress(deliveryAddress)
                .build();
    }

}
