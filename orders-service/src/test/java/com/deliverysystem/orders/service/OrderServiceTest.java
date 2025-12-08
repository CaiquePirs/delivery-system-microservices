package com.deliverysystem.orders.service;

import com.deliverysystem.orders.client.representation.CustomerDTO;
import com.deliverysystem.orders.client.representation.DeliveryAddressDTO;
import com.deliverysystem.orders.client.representation.RestaurantDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.ItemOrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderHistoryResponseDTO;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderResponseDTO;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import com.deliverysystem.orders.controller.exception.OrderNotFoundException;
import com.deliverysystem.orders.controller.exception.RestaurantClosedException;
import com.deliverysystem.orders.controller.exception.UserNotAuthorizedException;
import com.deliverysystem.orders.event.publisher.OrderEventPublisher;
import com.deliverysystem.orders.event.representation.CustomerResponseEvent;
import com.deliverysystem.orders.event.representation.OrderResponseEvent;
import com.deliverysystem.orders.mapper.OrderMapper;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.model.PaymentData;
import com.deliverysystem.orders.model.enums.AuditStatus;
import com.deliverysystem.orders.model.enums.OrderStatus;
import com.deliverysystem.orders.model.enums.PaymentMethod;
import com.deliverysystem.orders.repository.OrderRepository;
import com.deliverysystem.orders.service.calculator.OrderCalculator;
import com.deliverysystem.orders.service.validator.AccessValidator;
import com.deliverysystem.orders.service.validator.OrderValidator;
import com.deliverysystem.orders.utils.TestUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private ApiClientService apiClientService;
    @Mock private ItemOrderService itemOrderService;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderEventPublisher eventPublisher;
    @Mock private OrderValidator validator;
    @Mock private AccessValidator accessValidator;
    @Spy private OrderMapper mapper;
    @Spy private OrderCalculator calculator;
    @InjectMocks private OrderService orderService;

    private DeliveryAddressDTO deliveryAddress;
    private RestaurantDTO restaurantOrder;
    private CustomerDTO customerLogged;
    private OrderRequestDTO requestDTO;
    private  UUID customerIdLogged;
    private List<ItemsOrder> itemsOrders;


    @BeforeEach
    void setUp(){
        deliveryAddress = TestUtils.mockAddress();
        restaurantOrder = TestUtils.mockRestaurant();
        customerLogged = TestUtils.mockCustomer();

        customerIdLogged = customerLogged.id();
        itemsOrders = TestUtils.mockItemsOrders();

        requestDTO = new OrderRequestDTO(
                deliveryAddress.id(),
                restaurantOrder.id(),
                "Not pikles",
                new PaymentData("38372830293847", PaymentMethod.CARD),
                List.of(new ItemOrderRequestDTO(2, restaurantOrder.menus().get(0).id()))
        );
    }

    @Test
    void shouldCreateOrderSuccessfully(){
        when(accessValidator.getCustomerIdLogged()).thenReturn(customerIdLogged);
        when(apiClientService.findCustomerById(customerIdLogged)).thenReturn(CompletableFuture.completedFuture(customerLogged));
        when(apiClientService.findRestaurantById(requestDTO.restaurantId())).thenReturn(CompletableFuture.completedFuture(restaurantOrder));
        doNothing().when(validator).validateIfRestaurantIsOpen(restaurantOrder.status());
        when(validator.resolveDeliveryAddress(requestDTO.deliveryAddressId(), customerLogged)).thenReturn(deliveryAddress);
        when(itemOrderService.createItemsOrder(restaurantOrder, requestDTO.itemsDTO())).thenReturn(itemsOrders);
        when(calculator.calculateTotalOrder(itemsOrders)).thenReturn(BigDecimal.valueOf(90.00));

        Order orderEntityMapped = new Order();
        when(mapper.mapToEntity(requestDTO, itemsOrders, BigDecimal.valueOf(90.00))).thenReturn(orderEntityMapped);

        Order orderCreated = Order.builder()
                .id(new ObjectId())
                .notes(requestDTO.notes())
                .paymentData(requestDTO.paymentData())
                .status(OrderStatus.PENDING_PAYMENT)
                .orderDate(LocalDate.now())
                .itemsOrder(itemsOrders)
                .total(BigDecimal.valueOf(90.00))
                .restaurantId(requestDTO.restaurantId())
                .deliveryAddressId(requestDTO.deliveryAddressId())
                .estimatedDelivery(LocalDateTime.now().plusHours(2))
                .auditStatus(AuditStatus.ACTIVE)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        when(orderRepository.save(orderEntityMapped)).thenReturn(orderCreated);

        OrderResponseEvent orderResponseEvent = OrderResponseEvent.builder()
                .id(orderCreated.getId().toString())
                .restaurantId(orderCreated.getRestaurantId())
                .items(orderCreated.getItemsOrder())
                .status(orderCreated.getStatus())
                .total(orderCreated.getTotal())
                .orderDate(orderCreated.getOrderDate())
                .estimated_delivery(orderCreated.getEstimatedDelivery())
                .notes(orderCreated.getNotes())
                .customer(new CustomerResponseEvent(
                        UUID.randomUUID(),
                        customerLogged.name(),
                        customerLogged.email(),
                        customerLogged.phone(),
                        deliveryAddress
                ))
                .paymentData(orderCreated.getPaymentData())
                .build();

        when(mapper.mapToEventResponse(orderCreated, customerLogged, deliveryAddress)).thenReturn(orderResponseEvent);

        doNothing().when(eventPublisher).publishInVerifyPayment(any(OrderResponseEvent.class));

        assertNotNull(requestDTO);
        assertNotNull(customerLogged);
        assertNotNull(restaurantOrder);
        assertNotNull(itemsOrders);

        assertDoesNotThrow(() -> orderService.createOrder(requestDTO));

        verify(orderRepository, times(1)).save(orderEntityMapped);
        verify(eventPublisher, times(1)).publishInVerifyPayment(orderResponseEvent);
        assertNotNull(orderEntityMapped);
        assertNotNull(orderCreated);
        assertNotNull(orderResponseEvent);
        assertEquals(OrderStatus.PENDING_PAYMENT, orderCreated.getStatus());
        assertEquals(BigDecimal.valueOf(90.00), orderCreated.getTotal());
    }


    @Test
    void shouldTrowExceptionWhenGetCustomerLogged(){
       String exceptionMessage = "Customer is not authorized for perform this request";

        doThrow(new UserNotAuthorizedException(exceptionMessage))
                .when(accessValidator).getCustomerIdLogged();

        UserNotAuthorizedException ex = assertThrows(
                UserNotAuthorizedException.class,
                () -> orderService.createOrder(requestDTO)
        );

        assertEquals(exceptionMessage, ex.getMessage());

        verify(accessValidator, times(1)).getCustomerIdLogged();
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishInVerifyPayment(any(OrderResponseEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerIsNotFound(){
        when(accessValidator.getCustomerIdLogged()).thenReturn(customerIdLogged);

        String messageException = String.format("Customer ID: %s not found", customerIdLogged);

        doThrow(new ClientNotFoundException(messageException))
                .when(apiClientService).findCustomerById(customerIdLogged);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderService.createOrder(requestDTO)
        );
        assertEquals(messageException, ex.getMessage());

        verify(accessValidator, times(1)).getCustomerIdLogged();
        verify(apiClientService, times(1)).findCustomerById(customerIdLogged);
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishInVerifyPayment(any(OrderResponseEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIsNotFound(){
        when(accessValidator.getCustomerIdLogged()).thenReturn(customerIdLogged);
        when(apiClientService.findCustomerById(customerIdLogged)).thenReturn(CompletableFuture.completedFuture(customerLogged));

        String messageException = String.format("Restaurant ID: %s not found", requestDTO.restaurantId());

        doThrow(new ClientNotFoundException(messageException))
                .when(apiClientService).findRestaurantById(requestDTO.restaurantId());

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderService.createOrder(requestDTO)
        );
        assertEquals(messageException, ex.getMessage());

        verify(accessValidator, times(1)).getCustomerIdLogged();
        verify(apiClientService, times(1)).findCustomerById(customerIdLogged);
        verify(apiClientService, times(1)).findRestaurantById(requestDTO.restaurantId());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishInVerifyPayment(any(OrderResponseEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIsClosed(){
        when(accessValidator.getCustomerIdLogged()).thenReturn(customerIdLogged);
        when(apiClientService.findCustomerById(customerIdLogged)).thenReturn(CompletableFuture.completedFuture(customerLogged));
        when(apiClientService.findRestaurantById(requestDTO.restaurantId())).thenReturn(CompletableFuture.completedFuture(restaurantOrder));

        String messageException = "The selected restaurant is currently closed for orders.";

         doThrow(new RestaurantClosedException(messageException))
                .when(validator).validateIfRestaurantIsOpen(restaurantOrder.status());

        RestaurantClosedException ex = assertThrows(
                RestaurantClosedException.class,
                () -> orderService.createOrder(requestDTO)
        );

        assertEquals(messageException, ex.getMessage());

        verify(validator, times(1)).validateIfRestaurantIsOpen(restaurantOrder.status());
        verify(accessValidator, times(1)).getCustomerIdLogged();
        verify(apiClientService, times(1)).findCustomerById(customerIdLogged);
        verify(apiClientService, times(1)).findRestaurantById(requestDTO.restaurantId());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishInVerifyPayment(any(OrderResponseEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenTryResolveDeliveryAddress(){
        when(accessValidator.getCustomerIdLogged()).thenReturn(customerIdLogged);
        when(apiClientService.findCustomerById(customerIdLogged)).thenReturn(CompletableFuture.completedFuture(customerLogged));
        when(apiClientService.findRestaurantById(requestDTO.restaurantId())).thenReturn(CompletableFuture.completedFuture(restaurantOrder));
        doNothing().when(validator).validateIfRestaurantIsOpen(restaurantOrder.status());

        String messageException = String.format("Address ID: %s not found", requestDTO.restaurantId());
        doThrow(new ClientNotFoundException(messageException)).when(validator)
                .resolveDeliveryAddress(requestDTO.deliveryAddressId(), customerLogged);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderService.createOrder(requestDTO)
        );
        assertEquals(messageException, ex.getMessage());

        verify(validator, times(1)).resolveDeliveryAddress(requestDTO.deliveryAddressId(), customerLogged);
        verify(validator, times(1)).validateIfRestaurantIsOpen(restaurantOrder.status());
        verify(accessValidator, times(1)).getCustomerIdLogged();
        verify(apiClientService, times(1)).findCustomerById(customerIdLogged);
        verify(apiClientService, times(1)).findRestaurantById(requestDTO.restaurantId());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishInVerifyPayment(any(OrderResponseEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenMenuIsNotFound(){
        when(accessValidator.getCustomerIdLogged()).thenReturn(customerIdLogged);
        when(apiClientService.findCustomerById(customerIdLogged)).thenReturn(CompletableFuture.completedFuture(customerLogged));
        when(apiClientService.findRestaurantById(requestDTO.restaurantId())).thenReturn(CompletableFuture.completedFuture(restaurantOrder));
        doNothing().when(validator).validateIfRestaurantIsOpen(restaurantOrder.status());
        when(validator.resolveDeliveryAddress(requestDTO.deliveryAddressId(), customerLogged)).thenReturn(deliveryAddress);

        String messageException = String.format("Menu ID: %s not found", requestDTO.itemsDTO().get(0).menuId());
        doThrow(new ClientNotFoundException(messageException))
                .when(itemOrderService).createItemsOrder(restaurantOrder, requestDTO.itemsDTO());

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderService.createOrder(requestDTO)
        );
        assertEquals(messageException, ex.getMessage());

        verify(itemOrderService, times(1)).createItemsOrder(restaurantOrder, requestDTO.itemsDTO());
        verify(validator, times(1)).resolveDeliveryAddress(requestDTO.deliveryAddressId(), customerLogged);
        verify(validator, times(1)).validateIfRestaurantIsOpen(restaurantOrder.status());
        verify(accessValidator, times(1)).getCustomerIdLogged();
        verify(apiClientService, times(1)).findCustomerById(customerIdLogged);
        verify(apiClientService, times(1)).findRestaurantById(requestDTO.restaurantId());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishInVerifyPayment(any(OrderResponseEvent.class));
    }

    @Test
    void shouldFindOrderIdSuccessfully(){
        ObjectId orderId = new ObjectId();
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = assertDoesNotThrow(() -> orderService.findOrderById(orderId.toString()));
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenOrderIdIsNotFound(){
        ObjectId orderId = new ObjectId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.findOrderById(orderId.toString())
        );
        assertEquals("Order ID not found", ex.getMessage());

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldFindOrderResponseByIdSuccessfully(){
        ObjectId orderId = new ObjectId();

        Order orderExpected = Order.builder()
                .id(orderId)
                .notes("Order Notes")
                .status(OrderStatus.PENDING_PAYMENT)
                .orderDate(LocalDate.now())
                .itemsOrder(itemsOrders)
                .total(BigDecimal.valueOf(90.00))
                .restaurantId(UUID.randomUUID())
                .deliveryAddressId(UUID.randomUUID())
                .estimatedDelivery(LocalDateTime.now().plusHours(2))
                .auditStatus(AuditStatus.ACTIVE)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderExpected));
        when(apiClientService.findCustomerById(orderExpected.getCustomerId())).thenReturn(CompletableFuture.completedFuture(customerLogged));
        when(apiClientService.findRestaurantById(orderExpected.getRestaurantId())).thenReturn(CompletableFuture.completedFuture(restaurantOrder));
        when(validator.resolveDeliveryAddress(orderExpected.getDeliveryAddressId(), customerLogged)).thenReturn(deliveryAddress);

        OrderResponseDTO orderResult = assertDoesNotThrow(() -> orderService.findOrderResponseById(orderId.toString()));
        assertAll(
                () -> assertNotNull(orderResult),
                () -> assertNotNull(orderResult.getCustomer()),
                () -> assertNotNull(orderResult.getRestaurantEmail()),
                () -> assertEquals(orderId.toString(), orderResult.getId())
        );

        verify(orderRepository, times(1)).findById(orderId);
        verify(apiClientService, times(1)).findRestaurantById(orderExpected.getRestaurantId());
        verify(apiClientService, times(1)).findCustomerById(orderExpected.getCustomerId());
        verify(validator, times(1)).resolveDeliveryAddress(orderExpected.getDeliveryAddressId(), customerLogged);
    }

    @Test
    void shouldThrowExceptionWhenTryFindOrderResponseAndOrderIsIsNotFound(){
        ObjectId orderId = new ObjectId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.findOrderResponseById(orderId.toString())
        );
        assertEquals("Order ID not found", ex.getMessage());

        verify(orderRepository, times(1)).findById(orderId);
        verify(apiClientService, never()).findRestaurantById(any(UUID.class));
        verify(apiClientService, never()).findCustomerById(any(UUID.class));
        verify(validator, never()).resolveDeliveryAddress(any(UUID.class), any(CustomerDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenTryFindOrderResponseByIdAndCustomerIsNotFound(){
        ObjectId orderId = new ObjectId();

        Order order = Order.builder()
                .id(orderId)
                .customerId(UUID.randomUUID())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        String messageException = String.format("Customer ID: %s not found", order.getCustomerId());

        doThrow(new ClientNotFoundException(messageException))
                .when(apiClientService).findCustomerById(order.getCustomerId());

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderService.findOrderResponseById(orderId.toString())
        );
        assertEquals(messageException, ex.getMessage());

        verify(orderRepository, times(1)).findById(orderId);
        verify(apiClientService, never()).findRestaurantById(any(UUID.class));
        verify(apiClientService, times(1)).findCustomerById(order.getCustomerId());
        verify(validator, never()).resolveDeliveryAddress(any(UUID.class), any(CustomerDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenTryFindOrderResponseByIdAndRestaurantIsNotFound(){
        ObjectId orderId = new ObjectId();

        Order order = Order.builder()
                .id(orderId)
                .customerId(UUID.randomUUID())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(apiClientService.findCustomerById(order.getCustomerId())).thenReturn(CompletableFuture.completedFuture(customerLogged));

        String messageException = String.format("Restaurant ID: %s not found", order.getRestaurantId());

        doThrow(new ClientNotFoundException(messageException))
                .when(apiClientService).findRestaurantById(order.getRestaurantId());

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderService.findOrderResponseById(orderId.toString())
        );
        assertEquals(messageException, ex.getMessage());

        verify(orderRepository, times(1)).findById(orderId);
        verify(apiClientService, times(1)).findRestaurantById(order.getRestaurantId());
        verify(apiClientService, times(1)).findCustomerById(order.getCustomerId());
        verify(validator, never()).resolveDeliveryAddress(any(UUID.class), any(CustomerDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenTryFindOrderResponseByIdAndDeliveryAddressIsNotFound(){
        ObjectId orderId = new ObjectId();

        Order orderExpected = Order.builder()
                .id(orderId)
                .customerId(UUID.randomUUID())
                .restaurantId(UUID.randomUUID())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderExpected));
        when(apiClientService.findCustomerById(orderExpected.getCustomerId())).thenReturn(CompletableFuture.completedFuture(customerLogged));
        when(apiClientService.findRestaurantById(orderExpected.getRestaurantId())).thenReturn(CompletableFuture.completedFuture(restaurantOrder));

        String messageException = String.format("Address ID: %s not found", orderExpected.getDeliveryAddressId());
        doThrow(new ClientNotFoundException(messageException)).when(validator)
                .resolveDeliveryAddress(orderExpected.getDeliveryAddressId(), customerLogged);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderService.findOrderResponseById(orderId.toString())
        );
        assertEquals(messageException, ex.getMessage());

        verify(orderRepository, times(1)).findById(orderId);
        verify(apiClientService, times(1)).findRestaurantById(orderExpected.getRestaurantId());
        verify(apiClientService, times(1)).findCustomerById(orderExpected.getCustomerId());
        verify(validator, times(1)).resolveDeliveryAddress(orderExpected.getDeliveryAddressId(), customerLogged);
    }


    @Test
    void shouldFindAllOrderByCustomerIdSuccessfully(){
        UUID customerId = customerLogged.id();

        Order order1 = TestUtils.mockOrder(); order1.setCustomerId(customerId);
        Order order2 = TestUtils.mockOrder(); order2.setCustomerId(customerId);
        Order order3 = TestUtils.mockOrder(); order3.setCustomerId(customerId);

        List<Order> orders = List.of(order1, order2, order3);
        Pageable pageable = PageRequest.of(1, 3);
        Page<Order> pageOrders = new PageImpl<>(orders, pageable, orders.size());

        when(accessValidator.isCustomerOwner(customerId)).thenReturn(true);
        when(orderRepository.findAllByCustomerId(customerId, pageable)).thenReturn(pageOrders);

        Page<OrderHistoryResponseDTO> result = assertDoesNotThrow(
                () -> orderService.findAllOrdersByCustomerID(customerId, pageable));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.getSize()),
                () -> assertTrue(result.stream().anyMatch(order -> order.customerId().equals(customerId)))
        );

        verify(orderRepository, times(1)).findAllByCustomerId(customerId, pageable);
        verify(accessValidator, times(1)).isCustomerOwner(customerId);
    }

    @Test
    void shouldThrowExceptionWhenTryFindAllOrderByCustomerAndIsNotAuthorized(){
        UUID customerId = customerLogged.id();
        Pageable pageable = PageRequest.of(1, 3);

        when(accessValidator.isCustomerOwner(customerId)).thenReturn(false);

        UserNotAuthorizedException ex = assertThrows(
                UserNotAuthorizedException.class,
                () -> orderService.findAllOrdersByCustomerID(customerId, pageable)
        );
        assertEquals("Customer is not authorized for perform this request", ex.getMessage());

        verify(accessValidator, times(1)).isCustomerOwner(customerId);
        verify(orderRepository, never()).findAllByCustomerId(customerId, pageable);
    }

    @Test
    void shouldFindAllOrderByRestaurantIdSuccessfully(){
        UUID restaurantId = UUID.randomUUID();

        Order order1 = TestUtils.mockOrder(); order1.setRestaurantId(restaurantId);
        Order order2 = TestUtils.mockOrder(); order2.setRestaurantId(restaurantId);
        Order order3 = TestUtils.mockOrder(); order3.setRestaurantId(restaurantId);

        List<Order> orders = List.of(order1, order2, order3);
        Pageable pageable = PageRequest.of(1, 3);
        Page<Order> pageOrders = new PageImpl<>(orders, pageable, orders.size());

        when(accessValidator.isRestaurantOwner(restaurantId)).thenReturn(true);
        when(orderRepository.findAllByRestaurantId(restaurantId, pageable)).thenReturn(pageOrders);

        Page<OrderHistoryResponseDTO> result = assertDoesNotThrow(
                () -> orderService.findAllOrdersByRestaurantID(restaurantId, pageable)
        );

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.getSize()),
                () -> assertTrue(result.stream().anyMatch(order -> order.restaurantId().equals(restaurantId)))
        );

        verify(orderRepository, times(1)).findAllByRestaurantId(restaurantId, pageable);
        verify(accessValidator, times(1)).isRestaurantOwner(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenTryFindAllOrderByRestaurantAndIsNotAuthorized(){
        UUID restaurantId = customerLogged.id();
        Pageable pageable = PageRequest.of(1, 3);

        when(accessValidator.isRestaurantOwner(restaurantId)).thenReturn(false);

        UserNotAuthorizedException ex = assertThrows(
                UserNotAuthorizedException.class,
                () -> orderService.findAllOrdersByRestaurantID(restaurantId, pageable)
        );
        assertEquals("Restaurant is not authorized for perform this request", ex.getMessage());

        verify(accessValidator, times(1)).isRestaurantOwner(restaurantId);
        verify(orderRepository, never()).findAllByRestaurantId(restaurantId, pageable);
    }
}
