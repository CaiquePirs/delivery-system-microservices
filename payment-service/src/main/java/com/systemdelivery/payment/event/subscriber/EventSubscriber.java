package com.systemdelivery.payment.event.subscriber;

import com.systemdelivery.payment.event.representational.ProcessOrderPaymentEvent;
import com.systemdelivery.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventSubscriber {

    private final PaymentService paymentService;

    @RabbitListener(queues = "${spring.rabbitmq.subscriber.payment-queue}")
    public void subscriber(ProcessOrderPaymentEvent orderEventDTO){
        try {
            paymentService.processPayment(orderEventDTO);

        } catch (Exception e){
            log.error("Error receiving orderId: {} event with the error: {}", orderEventDTO.id(), e.getStackTrace());
        }
    }
}
