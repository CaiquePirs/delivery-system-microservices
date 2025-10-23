package com.deliveysistem.notification.strategy.factory;

import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.strategy.NotificationStrategy;
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
