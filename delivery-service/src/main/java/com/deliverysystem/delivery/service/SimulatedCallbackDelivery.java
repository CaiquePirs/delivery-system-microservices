package com.deliverysystem.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimulatedCallbackDelivery {

    public void simulateWebhook(UUID deliveryId) {
        try {
            Thread.sleep(Duration.ofMinutes(1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity("http://localhost:8087/api/deliveries/webhook/" + deliveryId, null, Void.class);

        } catch (Exception e){
            log.error("Error during simulated webhook for delivery ID {}: {}", deliveryId, e.getMessage());
        }
    }
}
