package com.deliveysistem.notification_service.model;

import com.deliveysistem.notification_service.event.representation.OrderEventDTO;
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
    private String subject;
    private OrderEventDTO body;
    private NotificationType notificationType;
}
