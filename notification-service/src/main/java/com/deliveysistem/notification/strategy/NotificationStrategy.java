package com.deliveysistem.notification.strategy;

import com.deliveysistem.notification.model.Notification;

public interface NotificationStrategy {
    void send(Notification notification);
}
