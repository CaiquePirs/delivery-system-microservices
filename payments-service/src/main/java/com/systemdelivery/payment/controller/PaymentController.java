package com.systemdelivery.payment.controller;

import com.systemdelivery.payment.gateway.dto.PaymentWebhookDTO;
import com.systemdelivery.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Void> receivePayment(@RequestBody PaymentWebhookDTO webhookDTO){
        paymentService.callbackPayment(webhookDTO);
        return ResponseEntity.noContent().build();
    }

}
