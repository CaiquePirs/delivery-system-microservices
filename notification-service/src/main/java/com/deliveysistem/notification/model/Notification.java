package com.deliveysistem.notification.model;

import com.deliveysistem.notification.event.representation.OrderEventDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private String to;
    private NotificationMessage message;
    private OrderEventDTO body;
    private NotificationType notificationType;
}
