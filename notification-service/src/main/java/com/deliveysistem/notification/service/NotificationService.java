package com.deliveysistem.notification.service;

import com.deliveysistem.notification.client.service.OrderClientApiService;
import com.deliveysistem.notification.event.representation.OrderEvent;
import com.deliveysistem.notification.event.representation.PaymentConfirmedEvent;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.NotificationMessage;
import com.deliveysistem.notification.model.Recipient;
import com.deliveysistem.notification.model.enums.NotificationType;
import com.deliveysistem.notification.model.enums.RecipientType;
import com.deliveysistem.notification.strategy.factory.NotificationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationFactory notificationFactory;
    private final OrderClientApiService orderClientApiService;

    public void sendNotificationOrderConfirmed(OrderEvent event) {
        Notification notification = Notification.builder()
                .body(event)
                .notificationType(NotificationType.EMAIL)
                .recipients(List.of(new Recipient(event.getCustomer().email(), RecipientType.CUSTOMER)))
                .message(new NotificationMessage("Order confirmed ✅", "has been successfully received on"))
                .build();

        notificationFactory.send(notification);
    }

    public void sendNotificationPaymentApproved(PaymentConfirmedEvent event){
        OrderEvent orderEvent = orderClientApiService.findOrderById(event.orderId());
        orderEvent.setStatus(event.status());

        List<Recipient> recipients = List.of(
                new Recipient(orderEvent.getCustomer().email(), RecipientType.CUSTOMER),
                new Recipient(orderEvent.getRestaurantEmail(), RecipientType.RESTAURANT)
        );

        Notification notification = Notification.builder()
                .body(orderEvent)
                .notificationType(NotificationType.EMAIL)
                .recipients(recipients)
                .message(new NotificationMessage("Order payment approved 💳", "The payment has been successfully confirmed and has been sent for processing on"))
                .build();

        notificationFactory.send(notification);
    }

}
