package com.systemdelivery.payment.service;

import com.systemdelivery.payment.event.publisher.PaymentEventPublisher;
import com.systemdelivery.payment.event.representational.ProcessOrderPaymentEvent;
import com.systemdelivery.payment.gateway.PaymentGateway;
import com.systemdelivery.payment.gateway.dto.PaymentWebhookDTO;
import com.systemdelivery.payment.model.Payment;
import com.systemdelivery.payment.model.PaymentData;
import com.systemdelivery.payment.model.enums.PaymentMethod;
import com.systemdelivery.payment.model.enums.PaymentStatus;
import com.systemdelivery.payment.repository.PaymentRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentEventPublisher paymentEventPublisher;
    @Mock private PaymentGateway paymentGateway;
    @InjectMocks private PaymentService paymentService;

    @Test
    void shouldProcessPaymentByEventSuccessfully() {
        ProcessOrderPaymentEvent paymentEvent = new ProcessOrderPaymentEvent(
                UUID.randomUUID().toString(),
                new PaymentData("938475", PaymentMethod.CARD, "X89POSJE"),
                BigDecimal.valueOf(50.00)
        );

        Payment paymentMock = Payment.builder()
                .orderId(paymentEvent.id())
                .paymentData(paymentEvent.paymentData())
                .status(PaymentStatus.PENDING)
                .amount(paymentEvent.total())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);

        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentMock);
        assertDoesNotThrow(() -> paymentService.processPayment(paymentEvent));

        verify(paymentRepository, times(1)).save(argumentCaptor.capture());
        Payment capturedPayment = argumentCaptor.getValue();

        assertAll(
                () -> assertEquals(paymentEvent.id(), capturedPayment.getOrderId()),
                () -> assertEquals(paymentEvent.paymentData(), capturedPayment.getPaymentData()),
                () -> assertEquals(paymentEvent.total(), capturedPayment.getAmount()),
                () -> assertEquals(PaymentStatus.PENDING, capturedPayment.getStatus()),
                () -> assertNotNull(capturedPayment.getCreated_at()),
                () -> assertNotNull(capturedPayment.getUpdated_at())
        );
        verify(paymentGateway, times(1)).pay(any(Payment.class));
        verify(paymentGateway, times(1)).pay(paymentMock);
    }

    @Test
    void ShouldMakeTheReturnPaymentSuccessfully(){
        PaymentWebhookDTO paymentWebhook = PaymentWebhookDTO.builder()
                .paymentId(new ObjectId().toString())
                .paymentKey("X89POSJE")
                .notes("Payment Authorized")
                .status(PaymentStatus.AUTHORIZED)
                .build();

        Payment paymentMock = Payment.builder()
                .id(new ObjectId(paymentWebhook.paymentId()))
                .orderId(UUID.randomUUID().toString())
                .paymentData(new PaymentData())
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(50.00))
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);

        when(paymentRepository.findById(paymentMock.getId())).thenReturn(Optional.of(paymentMock));
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentMock);
        doNothing().when(paymentEventPublisher).publisherInPaymentApproved(paymentMock);

        assertDoesNotThrow(() -> paymentService.callbackPayment(paymentWebhook));

        verify(paymentRepository, times(1)).save(argumentCaptor.capture());
        Payment capturedPayment = argumentCaptor.getValue();

        assertAll(
                () -> assertNotNull(paymentMock),
                () -> assertEquals(PaymentStatus.AUTHORIZED, capturedPayment.getStatus()),
                () -> assertEquals(paymentMock.getPaymentCode(), capturedPayment.getPaymentCode()),
                () -> assertNotNull(capturedPayment.getAmount()),
                () -> assertNotNull(capturedPayment.getUpdated_at())
        );

        verify(paymentRepository, times(1)).findById(paymentMock.getId());
        verify(paymentRepository, times(1)).save(paymentMock);
        verify(paymentEventPublisher, times(1)).publisherInPaymentApproved(paymentMock);
    }

    @Test
    void shouldNotCallBackPaymentWhenPaymentIsNotFoundAndReturnNull(){
        PaymentWebhookDTO paymentWebhook = PaymentWebhookDTO.builder()
                .paymentId(new ObjectId().toString())
                .paymentKey("X89POSJE")
                .notes("Payment Authorized")
                .status(PaymentStatus.AUTHORIZED)
                .build();

        when(paymentRepository.findById(new ObjectId(paymentWebhook.paymentId()))).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> paymentService.callbackPayment(paymentWebhook));

        verify(paymentRepository, times(1)).findById(new ObjectId(paymentWebhook.paymentId()));
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentEventPublisher, never()).publisherInPaymentApproved(any(Payment.class));
    }

    @Test
    void shouldNotCallBackPaymentWhenStatusViaWebhookIsNull(){
        PaymentWebhookDTO paymentWebhook = PaymentWebhookDTO.builder()
                .paymentId(new ObjectId().toString())
                .paymentKey("X89POSJE")
                .notes("Payment Authorized")
                .status(null)
                .build();

        Payment paymentMock = Payment.builder()
                .id(new ObjectId(paymentWebhook.paymentId()))
                .orderId(UUID.randomUUID().toString())
                .paymentData(new PaymentData())
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(50.00))
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();


        when(paymentRepository.findById(new ObjectId(paymentWebhook.paymentId()))).thenReturn(Optional.of(paymentMock));

        assertDoesNotThrow(() -> paymentService.callbackPayment(paymentWebhook));

        verify(paymentRepository, times(1)).findById(new ObjectId(paymentWebhook.paymentId()));
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentEventPublisher, never()).publisherInPaymentApproved(any(Payment.class));
    }

    @Test
    void ShouldMakeTheReturnPaymentFailedWhenIsFailToProcessPayment(){
        PaymentWebhookDTO paymentWebhook = PaymentWebhookDTO.builder()
                .paymentId(new ObjectId().toString())
                .paymentKey("X89POSJE")
                .notes("Payment Authorized")
                .status(PaymentStatus.FAILED)
                .build();

        Payment paymentMock = Payment.builder()
                .id(new ObjectId(paymentWebhook.paymentId()))
                .orderId(UUID.randomUUID().toString())
                .paymentData(new PaymentData())
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(50.00))
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);

        when(paymentRepository.findById(paymentMock.getId())).thenReturn(Optional.of(paymentMock));
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentMock);

        assertDoesNotThrow(() -> paymentService.callbackPayment(paymentWebhook));

        verify(paymentRepository, times(1)).save(argumentCaptor.capture());
        Payment capturedPayment = argumentCaptor.getValue();

        assertAll(
                () -> assertNotNull(paymentMock),
                () -> assertEquals(PaymentStatus.FAILED, capturedPayment.getStatus()),
                () -> assertEquals(paymentMock.getPaymentCode(), capturedPayment.getPaymentCode()),
                () -> assertNotNull(capturedPayment.getAmount()),
                () -> assertNotNull(capturedPayment.getUpdated_at())
        );

        verify(paymentRepository, times(1)).findById(paymentMock.getId());
        verify(paymentRepository, times(1)).save(paymentMock);
        verify(paymentEventPublisher, never()).publisherInPaymentApproved(paymentMock);
    }

}
