package com.deliverysystem.delivery.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public void processDeliveryForOrder(PaymentApprovedEvent event) {
        var order = clientApiService.findById(event.orderId());

        if (order == null) {
            log.error("Order with ID {} not found.", event.orderId());
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

    public void callbackDeliveryReady(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery not found with ID: " + deliveryId));

        if (delivery.getStatus().equals(DeliveryStatus.ASSIGNED)) {
            Currier currier = currierService.findAvailableCourierForDelivery();
            BigDecimal deliveryTax = deliveryTaxCalculator.calculateDeliveryTax(delivery.getTotalOrderAmount());

            delivery.setCurrier(currier);
            delivery.setDeliveryTax(deliveryTax);
            delivery.setActualDeliveryTime(LocalDateTime.now());
            delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);

            currier.getCompletedDeliveries().add(delivery);

            deliveryRepository.save(delivery);
            deliveryEventPublisher.publishEvent(delivery);

        } else throw new DeliveryErrorException(
                    String.format("Error processing delivery ID: %s , the current delivery status is: %s",
                            deliveryId, delivery.getStatus()));
    }

}
