package com.systemdelivery.payment.service;

import com.systemdelivery.payment.event.publisher.PaymentEventPublisher;
import com.systemdelivery.payment.event.representational.ProcessOrderPaymentEvent;
import com.systemdelivery.payment.gateway.SimulatedGatewayService;
import com.systemdelivery.payment.gateway.dto.PaymentWebhookDTO;
import com.systemdelivery.payment.model.Payment;
import com.systemdelivery.payment.model.enums.PaymentStatus;
import com.systemdelivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final SimulatedGatewayService simulatedGatewayService;

    public void processPayment(ProcessOrderPaymentEvent orderDTO){
        Payment payment = Payment.builder()
                .paymentData(orderDTO.paymentData())
                .status(PaymentStatus.PENDING)
                .amount(orderDTO.total())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        Payment paymentSaved = paymentRepository.save(payment);

        simulatedGatewayService.simulateCallback(paymentSaved.getId().toString());
    }

    public void callbackPayment(PaymentWebhookDTO webhookDTO){
        Payment payment = findPaymentById(webhookDTO.paymentId());

        if(webhookDTO.status() != null){
            if(webhookDTO.status().equals(PaymentStatus.FAILED)){
                Objects.requireNonNull(payment).setStatus(PaymentStatus.FAILED);
                payment.setUpdated_at(LocalDateTime.now());
                paymentRepository.save(payment);
            }

            if(webhookDTO.status().equals(PaymentStatus.AUTHORIZED)){
                Objects.requireNonNull(payment).setStatus(PaymentStatus.AUTHORIZED);
                payment.setPaymentCode(webhookDTO.paymentKey());
                payment.setNotes(webhookDTO.notes());
                payment.setUpdated_at(LocalDateTime.now());

                paymentEventPublisher.publisher(payment);

                paymentRepository.save(payment);
            }
        }
    }

    private Payment findPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(new ObjectId(paymentId))
                .orElse(null);

        if(payment == null){
            log.error("Error receiving payment webhook. PaymentID: {} not found.", paymentId);
        }
        return payment;
    }
}
