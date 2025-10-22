package com.deliveysistem.notification_service.event.subsbscriber;

import com.deliveysistem.notification_service.event.representation.OrderEventDTO;
import com.deliveysistem.notification_service.model.Notification;
import com.deliveysistem.notification_service.model.NotificationType;
import com.deliveysistem.notification_service.strategy.NotificationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventSubscriber {

    private final NotificationFactory notificationFactory;

    @RabbitListener(queues = "${spring.rabbitmq.notification-queue}")
    public void sendOrderConfirmationEmail(OrderEventDTO orderDTO){
        try {
            Notification notification = Notification.builder()
                    .to(orderDTO.customer().email())
                    .notificationType(NotificationType.EMAIL)
                    .subject("Order confirmed ✅")
                    .body(orderDTO)
                    .build();

            notificationFactory.send(notification);

        } catch (Exception e){
            log.error("Error when received order: {}, error: {}", orderDTO, e.getStackTrace());
        }
    }
}
