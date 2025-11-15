package com.systemdelivery.payment.gateway.impl;

import com.systemdelivery.payment.gateway.dto.PaymentWebhookDTO;
import com.systemdelivery.payment.model.enums.PaymentStatus;
import com.systemdelivery.payment.service.TokenClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulatedGateway {

    private final TokenClientService tokenClientService;;

    public void simulateCallback(String paymentId){
        // Simulate a delay for payment processing
        try {
            Thread.sleep(Duration.ofMinutes(1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        PaymentWebhookDTO webhook = PaymentWebhookDTO.builder()
                .paymentKey(UUID.randomUUID().toString())
                .status(PaymentStatus.AUTHORIZED)
                .paymentId(paymentId)
                .notes("Order Authorized Successfully")
                .build();

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(tokenClientService.getAccessToken());

            HttpEntity<PaymentWebhookDTO> request = new HttpEntity<>(webhook, headers);

            restTemplate.exchange(
                    "http://localhost:8080/api/payments/webhook",
                    HttpMethod.POST,
                    request,
                    Void.class
            );

        } catch (Exception e){
            log.error("Error when simulating payment callback: {}, for payment ID: {}", e.getMessage(), paymentId);
        }
    }
}
