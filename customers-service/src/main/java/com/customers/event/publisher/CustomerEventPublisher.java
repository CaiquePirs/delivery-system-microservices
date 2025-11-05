package com.customers.event.publisher;

import com.customers.event.representation.CustomerDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${CUSTOMERS_DELETED_QUEUE}")
    private String customerDeletedQueue;

    public void publisherCustomerDeleted(CustomerDeletedEvent event){
        try {
            rabbitTemplate.convertAndSend(customerDeletedQueue, event);

        } catch (Exception e){
            log.error("Error when publish customer deleted event: {}", e.getMessage());
        }
    }

}
