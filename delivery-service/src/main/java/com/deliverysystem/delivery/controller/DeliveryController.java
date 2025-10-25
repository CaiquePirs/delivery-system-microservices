package com.deliverysystem.delivery.controller;

import com.deliverysystem.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries/webhook")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/{id}")
    public ResponseEntity<Void> receiveDeliveryUpdate(@PathVariable("id") UUID deliveryId){
        deliveryService.callbackDeliveryReady(deliveryId);
        return ResponseEntity.noContent().build();
    }
}
