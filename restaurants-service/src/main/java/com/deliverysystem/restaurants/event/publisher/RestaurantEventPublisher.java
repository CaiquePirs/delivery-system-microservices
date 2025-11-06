package com.deliverysystem.restaurants.event.publisher;

import com.deliverysystem.restaurants.event.representation.RestaurantDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${RESTAURANT_DELETED_QUEUE}")
    private String restaurantDeletedQueue;

    public void publisherInRestaurantDeleted(RestaurantDeletedEvent event){
        try {
            rabbitTemplate.convertAndSend(restaurantDeletedQueue, event);

        } catch (Exception e){
            log.error("Error when publish restaurant deleted event: {}", e.getMessage());
        }
    }

}
