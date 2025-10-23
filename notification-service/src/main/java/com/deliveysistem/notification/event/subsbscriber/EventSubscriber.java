package com.deliveysistem.notification.event.subsbscriber;

import com.deliveysistem.notification.client.service.OrderClientApiService;
import com.deliveysistem.notification.event.representation.OrderEventDTO;
import com.deliveysistem.notification.event.representation.PaymentConfirmedEvent;
import com.deliveysistem.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventSubscriber {

    private final NotificationService notificationService;
    private final OrderClientApiService orderClientApiService;

    @RabbitListener(queues = "${spring.rabbitmq.order-confirmation-queue}")
    public void subscriberOrderConfirmation(OrderEventDTO event){
        try {
            String subject = "Order confirmed ✅";
            String text = "has been successfully received on";

            notificationService.sendNotificationByEmail(event, subject, text);

        } catch (Exception e){
            log.error("Error when received order: {}, error: {}", event, e.getStackTrace());
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.payment-approved-queue}")
    public void subscriberPaymentApproved(PaymentConfirmedEvent event){
        try {
            OrderEventDTO orderEvent = orderClientApiService.findOrderById(event.orderId());
            orderEvent.setStatus(event.status());

            String subject = "Order payment approved ✅💳";
            String text = "The payment has been successfully confirmed and has been sent for processing on";

            notificationService.sendNotificationByEmail(orderEvent, subject, text);
        } catch (Exception e){
            log.error("Error when received orderID: {}, error: {}", event.id(), e.getStackTrace());
        }
    }
}
