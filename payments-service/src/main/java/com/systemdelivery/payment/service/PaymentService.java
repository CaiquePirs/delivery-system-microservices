package com.systemdelivery.payment.service;

import com.systemdelivery.payment.event.publisher.PaymentEventPublisher;
import com.systemdelivery.payment.event.representational.ProcessOrderPaymentEvent;
import com.systemdelivery.payment.gateway.PaymentGateway;
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
    private final PaymentGateway paymentGateway;

    public void processPayment(ProcessOrderPaymentEvent orderDTO){
        Payment payment = Payment.builder()
                .orderId(orderDTO.id())
                .paymentData(orderDTO.paymentData())
                .status(PaymentStatus.PENDING)
                .amount(orderDTO.total())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        Payment paymentSaved = paymentRepository.save(payment);
        paymentGateway.pay(paymentSaved);
    }

    public void callbackPayment(PaymentWebhookDTO webhookDTO) {
        Payment payment = paymentRepository.findById(new ObjectId(webhookDTO.paymentId()))
                .orElse(null);

        if (payment != null) {
            if (webhookDTO.status() != null) {
                if (webhookDTO.status().equals(PaymentStatus.AUTHORIZED)) {
                    payment.setStatus(PaymentStatus.AUTHORIZED);
                    payment.setPaymentCode(webhookDTO.paymentKey());
                    payment.setNotes(webhookDTO.notes());
                    payment.setUpdated_at(LocalDateTime.now());

                    paymentEventPublisher.publisher(payment);
                    paymentRepository.save(payment);
                } else {
                    Objects.requireNonNull(payment).setStatus(PaymentStatus.FAILED);
                    payment.setUpdated_at(LocalDateTime.now());
                    paymentRepository.save(payment);
                }
            }
        } else {
            log.error("Payment not found with id: {}", webhookDTO.paymentId());
        }
    }
}
