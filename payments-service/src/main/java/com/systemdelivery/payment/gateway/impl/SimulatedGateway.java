package com.systemdelivery.payment.gateway.impl;

import com.systemdelivery.payment.gateway.dto.PaymentWebhookDTO;
import com.systemdelivery.payment.model.enums.PaymentStatus;
import com.systemdelivery.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;

@Service
@Slf4j
public class SimulatedGateway {

    private final PaymentService paymentService;

    public SimulatedGateway(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void simulateCallback(String paymentId){
        new Thread(() -> {
            try {
                Thread.sleep(Duration.ofMinutes(1).toMillis());

                PaymentWebhookDTO webhook = PaymentWebhookDTO.builder()
                        .paymentKey(UUID.randomUUID().toString())
                        .status(PaymentStatus.AUTHORIZED)
                        .paymentId(paymentId)
                        .notes("Order Authorized Successfully")
                        .build();

                paymentService.callbackPayment(webhook);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e){
                log.error("Error when simulating payment callback: {}, for payment ID: {}", e.getMessage(), paymentId);
            }
        }).start();
    }
}