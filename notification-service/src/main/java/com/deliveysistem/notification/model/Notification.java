package com.deliveysistem.notification.model;

import com.deliveysistem.notification.event.representation.OrderEvent;
import com.deliveysistem.notification.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private List<Recipient> recipients;
    private List<NotificationType> notificationTypes;
    private NotificationMessage message;
    private OrderEvent body;
}
