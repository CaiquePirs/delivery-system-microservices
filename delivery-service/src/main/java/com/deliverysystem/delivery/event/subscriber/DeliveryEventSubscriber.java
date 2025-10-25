package com.deliverysystem.delivery.event.subscriber;

import com.deliverysystem.delivery.event.representation.PaymentApprovedEvent;
import com.deliverysystem.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventSubscriber {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = "${spring.rabbitmq.subscribe-payment-queue}")
    public void subscribeEventPaymentApproved(PaymentApprovedEvent event) {
        try {
            deliveryService.processDeliveryForOrder(event);

        } catch (Exception e){
            log.error("Error subscribing to Payment Approved events: {}", e.getMessage());
        }
    }
}
