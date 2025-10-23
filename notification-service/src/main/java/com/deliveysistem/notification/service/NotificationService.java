package com.deliveysistem.notification.service;

import com.deliveysistem.notification.event.representation.OrderEventDTO;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.NotificationType;
import com.deliveysistem.notification.strategy.factory.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationFactory notificationFactory;

    public void sendNotificationByEmail(OrderEventDTO orderDTO, String subject, String text){
        Notification notification = Notification.builder()
                .to(orderDTO.getCustomer().email())
                .notificationType(NotificationType.EMAIL)
                .subject(subject)
                .text(text)
                .body(orderDTO)
                .build();

        notificationFactory.send(notification);
    }

}
