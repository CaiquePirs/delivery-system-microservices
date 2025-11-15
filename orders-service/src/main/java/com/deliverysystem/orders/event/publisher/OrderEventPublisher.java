package com.deliverysystem.orders.event.publisher;

import com.deliverysystem.orders.event.representation.OrderResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange-verify-payment}")
    private String exchangeKey;

    public void publishInVerifyPayment(OrderResponseEvent event){
        try {
            rabbitTemplate.convertAndSend(exchangeKey, "", event);

        } catch (Exception e){
            log.error("Error when publisher orderId: {}, with error: {}", event.id(), e.getStackTrace());
        }
    }
}
