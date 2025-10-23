package com.systemdelivery.payment.event.publisher;

import com.systemdelivery.payment.event.representational.PaymentConfirmedEvent;
import com.systemdelivery.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.publisher.payment-exchange}")
    private String exchangeKey;

    public void publisher(Payment payment){
        try {
            PaymentConfirmedEvent dto = PaymentConfirmedEvent.builder()
                    .id(payment.getId().toString())
                    .orderId(payment.getOrderId())
                    .amount(payment.getAmount())
                    .status(payment.getStatus().toString())
                    .build();

            rabbitTemplate.convertAndSend(exchangeKey, "", dto);

        } catch (Exception e){
            log.error("Error publishing paid order event, PaymentId: {}, Error: {}", payment.getId().toString(), e.getStackTrace());
        }
    }
}
