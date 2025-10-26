package com.deliverysystem.orders.service;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import com.deliverysystem.orders.client.representation.RestaurantRepresentationDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderResponseDTO;
import com.deliverysystem.orders.controller.exception.OrderNotFoundException;
import com.deliverysystem.orders.event.publisher.OrderEventPublisher;
import com.deliverysystem.orders.mapper.OrderMapper;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.repository.OrderRepository;
import com.deliverysystem.orders.service.calculator.OrderCalculator;
import com.deliverysystem.orders.service.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ApiClientService apiClientService;
    private final ItemOrderService itemOrderService;
    private final OrderCalculator calculator;
    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final OrderEventPublisher eventPublisher;
    private final OrderValidator validator;

    public void createOrder(OrderRequestDTO orderDTO){
        var customerFuture = apiClientService.findCustomerById(orderDTO.customerId());
        var restaurantFuture = apiClientService.findRestaurantById(orderDTO.restaurantId());
        CompletableFuture.allOf(customerFuture, restaurantFuture).join();

        CustomerRepresentationDTO customer = customerFuture.join();
        RestaurantRepresentationDTO restaurant = restaurantFuture.join();

        validator.validateIfRestaurantIsOpen(restaurant.status());
        AddressRepresentationDTO deliveryAddress = validator.resolveDeliveryAddress(orderDTO.deliveryAddressId(), customer);

        List<ItemsOrder> items = itemOrderService.createItemsOrder(restaurant, orderDTO.itemsDTO());
        BigDecimal totalOrder = calculator.calculateTotalOrder(items);

        Order orderEntity = mapper.mapToEntity(orderDTO, items, totalOrder);
        Order createdOrder = orderRepository.save(orderEntity);
        createdOrder.setPaymentData(orderDTO.paymentData());

        eventPublisher.publishVerifyPayment(mapper.mapToEventResponse(
                createdOrder,
                customer,
                deliveryAddress)
        );
    }

    public OrderResponseDTO findOrderResponseById(String orderId){
        Order order = findOrderById(orderId);

        var customerFuture = apiClientService.findCustomerById(order.getCustomerId());
        var restaurantFuture = apiClientService.findRestaurantById(order.getRestaurantId());
        CompletableFuture.allOf(customerFuture, restaurantFuture).join();

        CustomerRepresentationDTO customer = customerFuture.join();
        RestaurantRepresentationDTO restaurant = restaurantFuture.join();
        AddressRepresentationDTO deliveryAddress = validator.resolveDeliveryAddress(order.getDeliveryAddressId(), customer);

        OrderResponseDTO orderResponse = mapper.mapToResponse(order, customer, deliveryAddress);
        orderResponse.setRestaurantEmail(restaurant.email());

        return orderResponse;
    }

    public Order findOrderById(String orderId){
        return orderRepository.findById(new ObjectId(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Order ID not found"));
    }
}
