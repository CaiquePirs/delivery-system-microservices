package com.deliveysistem.notification_service.strategy;

import com.deliveysistem.notification_service.model.Notification;

public interface NotificationStrategy {
    void send(Notification notification);
}
