package com.deliveysistem.notification_service.strategy;

import com.deliveysistem.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final List<NotificationStrategy> strategies;

    public void send(Notification notification) {
        for (NotificationStrategy notificationStrategy : strategies) {
            notificationStrategy.send(notification);
        }
    }

}
