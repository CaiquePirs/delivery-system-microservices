package com.deliverysystem.orders.service;

import com.deliverysystem.orders.client.representation.DeliveryAddressDTO;
import com.deliverysystem.orders.client.representation.CustomerDTO;
import com.deliverysystem.orders.client.representation.RestaurantDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.OrderHistoryResponseDTO;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderResponseDTO;
import com.deliverysystem.orders.controller.exception.OrderNotFoundException;
import com.deliverysystem.orders.controller.exception.UserNotAuthorizedException;
import com.deliverysystem.orders.event.publisher.OrderEventPublisher;
import com.deliverysystem.orders.event.representation.OrderResponseEvent;
import com.deliverysystem.orders.mapper.OrderMapper;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.repository.OrderRepository;
import com.deliverysystem.orders.service.calculator.OrderCalculator;
import com.deliverysystem.orders.service.validator.AccessValidator;
import com.deliverysystem.orders.service.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
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
    private final AccessValidator accessValidator;

    public void createOrder(OrderRequestDTO orderDTO){
        UUID customerLoggedId = accessValidator.getCustomerIdLogged();

        CompletableFuture<CustomerDTO> customerFuture = apiClientService.findCustomerById(customerLoggedId);
        CompletableFuture<RestaurantDTO> restaurantFuture = apiClientService.findRestaurantById(orderDTO.restaurantId());
        CompletableFuture.allOf(customerFuture, restaurantFuture).join();

        CustomerDTO customer = customerFuture.join();
        RestaurantDTO restaurant = restaurantFuture.join();

        validator.validateIfRestaurantIsOpen(restaurant.status());
        DeliveryAddressDTO deliveryAddress = validator.resolveDeliveryAddress(orderDTO.deliveryAddressId(), customer);

        List<ItemsOrder> items = itemOrderService.createItemsOrder(restaurant, orderDTO.itemsDTO());
        BigDecimal totalOrder = calculator.calculateTotalOrder(items);

        Order orderEntity = mapper.mapToEntity(orderDTO, items, totalOrder);
        orderEntity.setCustomerId(customerLoggedId);

        Order createdOrder = orderRepository.save(orderEntity);
        createdOrder.setPaymentData(orderDTO.paymentData());

        OrderResponseEvent orderEvent = mapper.mapToEventResponse(createdOrder, customer, deliveryAddress);
        eventPublisher.publishInVerifyPayment(orderEvent);
    }

    public OrderResponseDTO findOrderResponseById(String orderId){
        Order order = findOrderById(orderId);

        CompletableFuture<CustomerDTO> customerFuture = apiClientService.findCustomerById(order.getCustomerId());
        CompletableFuture<RestaurantDTO> restaurantFuture = apiClientService.findRestaurantById(order.getRestaurantId());
        CompletableFuture.allOf(customerFuture, restaurantFuture).join();

        CustomerDTO customer = customerFuture.join();
        RestaurantDTO restaurant = restaurantFuture.join();
        DeliveryAddressDTO deliveryAddress = validator.resolveDeliveryAddress(order.getDeliveryAddressId(), customer);

        OrderResponseDTO orderResponse = mapper.mapToResponse(order, customer, deliveryAddress);
        orderResponse.setRestaurantEmail(restaurant.email());

        return orderResponse;
    }

    public Order findOrderById(String orderId){
        return orderRepository.findById(new ObjectId(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Order ID not found"));
    }

    public Page<OrderHistoryResponseDTO> findAllOrdersByCustomerID(UUID customerId, Pageable pageable) {
        if(!accessValidator.isCustomerOwner(customerId)){
            throw new UserNotAuthorizedException("Customer is not authorized for perform this request");
        }

        List<OrderHistoryResponseDTO> ordersPage = orderRepository.findAllByCustomerId(customerId, pageable)
                .stream()
                .map(mapper::mapToOrderHistoryResponse)
                .toList();

        return new PageImpl<>(ordersPage, pageable, ordersPage.size());
    }

    public Page<OrderHistoryResponseDTO> findAllOrdersByRestaurantID(UUID restaurantId, Pageable pageable) {
        if(!accessValidator.isRestaurantOwner(restaurantId)){
            throw new UserNotAuthorizedException("Restaurant is not authorized for perform this request");
        }

        List<OrderHistoryResponseDTO> ordersPage = orderRepository.findAllByRestaurantId(restaurantId, pageable)
                .stream()
                .map(mapper::mapToOrderHistoryResponse)
                .toList();

        return new PageImpl<>(ordersPage, pageable, ordersPage.size());
    }
}
