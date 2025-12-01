package com.deliverysystem.delivery.service;

import com.deliverysystem.delivery.client.representation.OrderDTO;
import com.deliverysystem.delivery.controller.advice.exceptions.DeliveryErrorException;
import com.deliverysystem.delivery.controller.advice.exceptions.NotFoundException;
import com.deliverysystem.delivery.client.ClientApiService;
import com.deliverysystem.delivery.event.publisher.DeliveryEventPublisher;
import com.deliverysystem.delivery.event.representation.PaymentApprovedEvent;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.model.Delivery;
import com.deliverysystem.delivery.model.enums.DeliveryStatus;
import com.deliverysystem.delivery.repositories.DeliveryRepository;
import com.deliverysystem.delivery.service.calculator.DeliveryTaxCalculator;
import com.deliverysystem.delivery.validator.DeliveryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final ClientApiService clientApiService;
    private final DeliveryRepository deliveryRepository;
    private final CurrierService currierService;
    private final DeliveryTaxCalculator deliveryTaxCalculator;
    private final DeliveryEventPublisher deliveryEventPublisher;
    private final DeliveryValidator deliveryValidator;

    public void processDeliveryForOrder(PaymentApprovedEvent event) {
        var order = clientApiService.findById(event.orderId());

        if(order == null){
            log.error("Error processing delivery for order ID: {}", event.orderId());
            return;
        }

        Delivery delivery = Delivery.builder()
                .orderId(order.id())
                .totalOrderAmount(order.total())
                .status(DeliveryStatus.ASSIGNED)
                .deliveryAddress(order.customer().deliveryAddress())
                .estimatedDeliveryTime(order.estimated_delivery())
                .build();

        deliveryRepository.save(delivery);
    }

    @Transactional
    public void callbackDeliveryReady(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        OrderDTO order = clientApiService.findById(delivery.getOrderId());

        if (order == null) {
            throw new NotFoundException("error processing delivery with ID: " + deliveryId + "order information is not found");
        }

        deliveryValidator.validateAuthenticatedRestaurantOwnership(order.restaurantId());

        if (delivery.getStatus().equals(DeliveryStatus.ASSIGNED)) {
            Currier currier = currierService.findAvailableCourierForDelivery();
            BigDecimal deliveryTax = deliveryTaxCalculator.calculateDeliveryTax(delivery.getTotalOrderAmount());

            delivery.setCurrier(currier);
            delivery.setDeliveryTax(deliveryTax);
            delivery.setActualDeliveryTime(LocalDateTime.now());
            delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);

            currier.getCompletedDeliveries().add(delivery);

            deliveryRepository.save(delivery);
            deliveryEventPublisher.publishDeliveryShipped(delivery);

        } else throw new DeliveryErrorException(deliveryId, delivery.getStatus());
    }

    private Delivery findDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery not found with ID: " + deliveryId));
    }

}
