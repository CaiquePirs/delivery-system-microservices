package com.deliverysystem.delivery.event.publisher;

import com.deliverysystem.delivery.event.representation.DeliveryReadyEvent;
import com.deliverysystem.delivery.model.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.publisher-delivery-ready}")
    private String exchangeKey;

    public void publishEvent(Delivery delivery) {
        try {
            DeliveryReadyEvent event = new DeliveryReadyEvent(
                   delivery.getId(),
                    delivery.getOrderId()
            );

            rabbitTemplate.convertAndSend(exchangeKey, "", event);

        } catch (Exception e){
            log.error("Error publishing Delivery Ready event for Delivery ID {}: {}", delivery.getId(), e.getMessage());
        }
    }

}
