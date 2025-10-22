package com.systemdelivery.payment.gateway;

import com.systemdelivery.payment.gateway.dto.PaymentWebhookDTO;
import com.systemdelivery.payment.model.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class SimulatedGatewayService {

    public void simulateCallback(String paymentId){
        try {
            Thread.sleep(Duration.ofMinutes(1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        PaymentWebhookDTO webhook = PaymentWebhookDTO.builder()
                .paymentKey(UUID.randomUUID().toString())
                .notes(String.format("SIMULATED-KEY-%s", paymentId))
                .status(PaymentStatus.AUTHORIZED)
                .paymentId(paymentId)
                .notes("Order Authorized Successfully")
                .build();

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity("http://localhost:8084/api/payments/webhook", webhook, Void.class);

        } catch (Exception e){
            log.error("Error ");
        }
    }
}
