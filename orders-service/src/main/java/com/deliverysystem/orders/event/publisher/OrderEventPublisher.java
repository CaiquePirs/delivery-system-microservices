package com.deliverysystem.orders.event.publisher;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import com.deliverysystem.orders.client.representation.CustomerRepresentationDTO;
import com.deliverysystem.orders.controller.dto.OrderEventPublisherDTO;
import com.deliverysystem.orders.mapper.OrderMapper;
import com.deliverysystem.orders.model.Order;
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
    private final OrderMapper orderMapper;

    @Value("${spring.rabbitmq.exchange-payment}")
    private String exchangeKey;

    public void publisher(Order order, CustomerRepresentationDTO customer, AddressRepresentationDTO deliveryAddress){
        try {
            OrderEventPublisherDTO orderEventDTO = orderMapper.mapToPublishEvent(order, customer, deliveryAddress);
            rabbitTemplate.convertAndSend(exchangeKey, "", orderEventDTO);

        } catch (Exception e){
            log.error("Error when publisher orderId: {}, with error: {}", order.getId(), e.getStackTrace());
        }
    }
}
