package com.deliveysistem.notification.event.subsbscriber;

import com.deliveysistem.notification.event.representation.OrderEventDTO;
import com.deliveysistem.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventSubscriber {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${spring.rabbitmq.notification-queue}")
    public void sendOrderConfirmation(OrderEventDTO orderDTO){
        try {
            String subject = "Order confirmed ✅";
            String text = "has been successfully received on";

            notificationService.sendNotificationByEmail(orderDTO, subject, text);

        } catch (Exception e){
            log.error("Error when received order: {}, error: {}", orderDTO, e.getStackTrace());
        }
    }
}
