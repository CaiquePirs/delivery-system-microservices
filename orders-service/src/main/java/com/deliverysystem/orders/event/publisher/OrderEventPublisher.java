package com.deliverysystem.orders.event.publisher;

import com.deliverysystem.orders.controller.dto.OrderEventPublisherDTO;
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

    @Value("${spring.rabbitmq.exchange-payment}")
    private String exchangeKey;

    public void publisher(OrderEventPublisherDTO orderDTO){
        try {
            rabbitTemplate.convertAndSend(exchangeKey, "", orderDTO);

        } catch (Exception e){
            log.error("Error when publisher orderId: {}, with error: {}", orderDTO.id(), e.getStackTrace());
        }
    }
}
