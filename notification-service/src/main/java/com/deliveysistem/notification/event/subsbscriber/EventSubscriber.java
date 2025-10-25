package com.deliveysistem.notification.event.subsbscriber;

import com.deliveysistem.notification.event.representation.DeliveryReadyEvent;
import com.deliveysistem.notification.event.representation.OrderEvent;
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

    @RabbitListener(queues = "${spring.rabbitmq.order-confirmation-queue}")
    public void subscriberOrderConfirmation(OrderEvent event){
        try {
            notificationService.sendNotificationOrderConfirmed(event);

        } catch (Exception e){
            log.error("Error when received order confirmed event, OrderId: {}, error: {}", event.getId(), e.getStackTrace());
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.payment-approved-queue}")
    public void subscriberPaymentApproved(PaymentConfirmedEvent event){
        try {
            notificationService.sendNotificationPaymentApproved(event);

        } catch (Exception e){
            log.error("Error when received payment approved event, OrderId: {}, error: {}", event.orderId(), e.getStackTrace());
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.delivery-ready-queue}")
    public void subscriberDeliveryReady(DeliveryReadyEvent event){
        try {
            notificationService.sendNotificationDeliveryReady(event);

        } catch (Exception e){
            log.error("Error when received delivery ready event, OrderId: {}, error: {}", event.orderId(), e.getStackTrace());
        }
    }

}
