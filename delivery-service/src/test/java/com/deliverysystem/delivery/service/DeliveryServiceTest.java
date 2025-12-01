package com.deliverysystem.delivery.service;

import com.deliverysystem.delivery.client.ClientApiService;
import com.deliverysystem.delivery.client.representation.CustomerDTO;
import com.deliverysystem.delivery.client.representation.OrderDTO;
import com.deliverysystem.delivery.controller.advice.exceptions.DeliveryErrorException;
import com.deliverysystem.delivery.controller.advice.exceptions.NotFoundException;
import com.deliverysystem.delivery.controller.advice.exceptions.RestaurantNotAuthorizedException;
import com.deliverysystem.delivery.event.publisher.DeliveryEventPublisher;
import com.deliverysystem.delivery.event.representation.PaymentApprovedEvent;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.model.Delivery;
import com.deliverysystem.delivery.model.enums.DeliveryStatus;
import com.deliverysystem.delivery.repositories.DeliveryRepository;
import com.deliverysystem.delivery.service.calculator.DeliveryTaxCalculator;
import com.deliverysystem.delivery.validator.DeliveryValidator;
import com.deliverysystem.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    @Mock private ClientApiService clientApiService;
    @Mock private DeliveryRepository deliveryRepository;
    @Mock private DeliveryEventPublisher deliveryEventPublisher;
    @Mock private DeliveryValidator deliveryValidator;
    @Mock private CurrierService currierService;
    @Mock private DeliveryTaxCalculator deliveryTaxCalculator;
    @InjectMocks private DeliveryService deliveryService;

    private CustomerDTO customerDTO;
    private UUID deliveryId;
    private Delivery delivery;

    @BeforeEach
    void setUp(){
        delivery =  TestUtils.deliveryAssigned();
        deliveryId = delivery.getId();

        customerDTO = TestUtils.customerDTO();
    }

    @Test
    void shouldProcessDeliveryForOrderSuccessfully(){
        PaymentApprovedEvent event = new PaymentApprovedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                BigDecimal.valueOf(200.00),
                "AUTHORIZED"
        );

        OrderDTO orderDTO = new OrderDTO(
                event.orderId(),
                UUID.randomUUID(),
                LocalDate.now(),
                BigDecimal.valueOf(200.00),
                "PAID",
                "No picles",
                LocalDateTime.now().plusHours(1),
                customerDTO
        );

        when(clientApiService.findById(event.orderId())).thenReturn(orderDTO);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(new Delivery());

        assertDoesNotThrow(() -> deliveryService.processDeliveryForOrder(event));
        assertNotNull(orderDTO);
        assertNotNull(orderDTO.customer());
        assertNotNull(orderDTO.customer().deliveryAddress());

        verify(clientApiService, times(1)).findById(event.orderId());
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void shouldCallbackDeliveryReadySuccessfully(){
        Delivery delivery = TestUtils.deliveryAssigned();
        OrderDTO orderDTO = TestUtils.orderDTO();
        Currier currier = TestUtils.currier();

        assertNull(delivery.getCurrier());
        assertNull(delivery.getActualDeliveryTime());
        assertNull(delivery.getDeliveryTax());
        assertEquals(DeliveryStatus.ASSIGNED, delivery.getStatus());

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        doNothing().when(deliveryValidator).validateAuthenticatedRestaurantOwnership(orderDTO.restaurantId());

        when(clientApiService.findById(delivery.getOrderId())).thenReturn(orderDTO);
        when(currierService.findAvailableCourierForDelivery()).thenReturn(currier);
        when(deliveryRepository.save(delivery)).thenReturn(new Delivery());
        when(deliveryTaxCalculator.calculateDeliveryTax(orderDTO.total())).thenReturn(BigDecimal.valueOf(16.00));

        doNothing().when(deliveryEventPublisher).publishDeliveryShipped(delivery);

        assertDoesNotThrow(() -> deliveryService.callbackDeliveryReady(deliveryId));

        assertEquals(DeliveryStatus.OUT_FOR_DELIVERY, delivery.getStatus());
        assertNotNull(delivery.getActualDeliveryTime());
        assertNotNull(delivery.getDeliveryTax());
        assertNotNull(delivery.getCurrier());

        verify(deliveryRepository, times(1)).save(delivery);
        verify(currierService, times(1)).findAvailableCourierForDelivery();
        verify(deliveryTaxCalculator, times(1)).calculateDeliveryTax(orderDTO.total());
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(deliveryEventPublisher, times(1)).publishDeliveryShipped(delivery);
    }

    @Test
    void shouldTrowExceptionWhenDeliveryIdIsNotFound(){
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> deliveryService.callbackDeliveryReady(deliveryId)
        );

        assertEquals("Delivery not found with ID: " + deliveryId, ex.getMessage());

        verify(deliveryRepository, never()).save(delivery);
        verify(deliveryEventPublisher, never()).publishDeliveryShipped(delivery);
        verify(currierService, never()).findAvailableCourierForDelivery();
        verify(deliveryTaxCalculator, never()).calculateDeliveryTax(any(BigDecimal.class));
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(clientApiService, never()).findById(delivery.getOrderId());
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNotFound(){
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(clientApiService.findById(delivery.getOrderId())).thenReturn(null);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> deliveryService.callbackDeliveryReady(deliveryId)
        );

        assertEquals(
                "error processing delivery with ID: " + deliveryId + "order information is not found",
                ex.getMessage()
        );

        verify(deliveryRepository, never()).save(delivery);
        verify(deliveryEventPublisher, never()).publishDeliveryShipped(delivery);
        verify(currierService, never()).findAvailableCourierForDelivery();
        verify(deliveryTaxCalculator, never()).calculateDeliveryTax(any(BigDecimal.class));
        verify(deliveryRepository, times(1)).findById(deliveryId);
    }

    @Test
    void shouldThrowExceptionWhenIsNotAuthenticatedRestaurantOwnership(){
        OrderDTO orderDTO = TestUtils.orderDTO();
        String messageException = "Restaurant not authorized to perform this request";

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(clientApiService.findById(delivery.getOrderId())).thenReturn(orderDTO);

        doThrow(new RestaurantNotAuthorizedException(messageException))
                .when(deliveryValidator).validateAuthenticatedRestaurantOwnership(orderDTO.restaurantId());

        RestaurantNotAuthorizedException ex = assertThrows(
                RestaurantNotAuthorizedException.class,
                () -> deliveryService.callbackDeliveryReady(deliveryId)
        );

        assertEquals(messageException, ex.getMessage());

        verify(deliveryValidator, times(1)).validateAuthenticatedRestaurantOwnership(orderDTO.restaurantId());
        verify(clientApiService, times(1)).findById(delivery.getOrderId());
        verify(deliveryRepository, never()).save(delivery);
        verify(deliveryEventPublisher, never()).publishDeliveryShipped(delivery);
        verify(currierService, never()).findAvailableCourierForDelivery();
        verify(deliveryTaxCalculator, never()).calculateDeliveryTax(any(BigDecimal.class));
        verify(deliveryRepository, times(1)).findById(deliveryId);
    }

    @Test
    void shouldThrowExceptionWhenDeliveryStatusIsNotAssigned(){
        OrderDTO orderDTO = TestUtils.orderDTO();
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(clientApiService.findById(delivery.getOrderId())).thenReturn(orderDTO);
        doNothing().when(deliveryValidator).validateAuthenticatedRestaurantOwnership(orderDTO.restaurantId());

        DeliveryErrorException ex = assertThrows(
                DeliveryErrorException.class,
                () -> deliveryService.callbackDeliveryReady(deliveryId)
        );
        assertEquals(
                String.format("Error processing delivery ID: %s , the current delivery status is: %s", deliveryId, delivery.getStatus()),
                ex.getMessage()
        );

        assertNotNull(orderDTO);
        assertEquals(DeliveryStatus.OUT_FOR_DELIVERY, delivery.getStatus());

        verify(clientApiService, times(1)).findById(delivery.getOrderId());
        verify(deliveryRepository, never()).save(delivery);
        verify(deliveryEventPublisher, never()).publishDeliveryShipped(delivery);
        verify(currierService, never()).findAvailableCourierForDelivery();
        verify(deliveryTaxCalculator, never()).calculateDeliveryTax(any(BigDecimal.class));
        verify(deliveryRepository, times(1)).findById(deliveryId);
    }



}
