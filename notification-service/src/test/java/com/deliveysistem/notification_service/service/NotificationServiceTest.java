package com.deliveysistem.notification_service.service;

import com.deliveysistem.notification.client.service.OrderClientApiService;
import com.deliveysistem.notification.event.representation.DeliveryReadyEvent;
import com.deliveysistem.notification.event.representation.OrderEvent;
import com.deliveysistem.notification.event.representation.PaymentConfirmedEvent;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.enums.NotificationType;
import com.deliveysistem.notification.model.enums.RecipientType;
import com.deliveysistem.notification.service.NotificationService;
import com.deliveysistem.notification.strategy.factory.NotificationFactory;
import com.deliveysistem.notification_service.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock private NotificationFactory notificationFactory;
    @Mock private OrderClientApiService orderClientApiService;
    @InjectMocks private NotificationService notificationService;

    @Test
    void shouldSendNotificationOrderConfirmedSuccessfullyByEmail(){
        OrderEvent orderEvent = TestUtils.orderEvent();
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        notificationService.sendNotificationOrderConfirmed(orderEvent);
        verify(notificationFactory, times(1)).send(captor.capture());

        Notification captured = captor.getValue();

        assertEquals("Order confirmed ✅", captured.getMessage().subject());
        assertEquals("has been successfully received on", captured.getMessage().text());
        assertEquals(1, captured.getRecipients().size());
        assertEquals(NotificationType.EMAIL, captured.getNotificationTypes().get(0));
        assertEquals(RecipientType.CUSTOMER, captured.getRecipients().get(0).getType());
        assertEquals(orderEvent, captured.getBody());
    }

    @Test
    void shouldSendNotificationPaymentApprovedByEmailSuccessfully(){
        PaymentConfirmedEvent paymentConfirmedEvent = TestUtils.paymentConfirmedEvent();
        OrderEvent orderEvent = TestUtils.orderEvent();
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        when(orderClientApiService.findOrderById(paymentConfirmedEvent.orderId())).thenReturn(orderEvent);
        notificationService.sendNotificationPaymentApproved(paymentConfirmedEvent);

        verify(notificationFactory, times(1)).send(captor.capture());

        Notification captured = captor.getValue();

        assertEquals("Order payment approved 💳", captured.getMessage().subject());
        assertEquals("The payment has been successfully confirmed and has been sent for processing on", captured.getMessage().text());
        assertEquals(2, captured.getRecipients().size());
        assertEquals(NotificationType.EMAIL, captured.getNotificationTypes().get(0));
        assertTrue(captured.getRecipients().stream().anyMatch(r -> r.getType().equals(RecipientType.CUSTOMER)));
        assertTrue(captured.getRecipients().stream().anyMatch(r -> r.getType().equals(RecipientType.RESTAURANT)));
        assertEquals(orderEvent, captured.getBody());
    }

    @Test
    void shouldSendNotificationDeliveryReadyByEmailSuccessfully(){
        DeliveryReadyEvent deliveryReadyEvent = TestUtils.deliveryReadyEvent();
        OrderEvent orderEvent = TestUtils.orderEvent();
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        when(orderClientApiService.findOrderById(deliveryReadyEvent.orderId())).thenReturn(orderEvent);
        notificationService.sendNotificationDeliveryReady(deliveryReadyEvent);

        verify(notificationFactory, times(1)).send(captor.capture());

        Notification captured = captor.getValue();

        assertEquals("Order is out for delivery 🚚", captured.getMessage().subject());
        assertEquals("Order is out for delivery Arriving on", captured.getMessage().text());
        assertEquals(1, captured.getRecipients().size());
        assertEquals(NotificationType.EMAIL, captured.getNotificationTypes().get(0));
        assertEquals(RecipientType.CUSTOMER, captured.getRecipients().get(0).getType());
        assertEquals(orderEvent, captured.getBody());
    }




}
