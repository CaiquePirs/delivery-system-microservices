package com.systemdelivery.payment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdelivery.payment.gateway.dto.PaymentWebhookDTO;
import com.systemdelivery.payment.model.Payment;
import com.systemdelivery.payment.model.PaymentData;
import com.systemdelivery.payment.model.enums.PaymentMethod;
import com.systemdelivery.payment.model.enums.PaymentStatus;
import com.systemdelivery.payment.repository.PaymentRepository;
import com.systemdelivery.payment.service.PaymentService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired private PaymentService paymentService;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private Payment payment;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("RESTAURANT_DELETED_QUEUE", () -> "restaurant-deleted-queue");
        registry.add("KEYCLOAK_JWK_URI", () -> "http://localhost:8081/realms/master/protocol/openid-connect/certs");
        registry.add("KEYCLOAK_REALM", () -> "master");
        registry.add("KEYCLOAK_CLIENT_ID", () -> "admin-cli");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "dummy");
        registry.add("SERVICE_TOKEN_URL", () -> "http://localhost:8082/mock-token");
    }

    @BeforeEach
    void cleanBeforeEach(){
        payment = Payment.builder()
                .orderId(UUID.randomUUID().toString())
                .paymentData(new PaymentData("83838383", PaymentMethod.CARD, "XPL9389"))
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(30.98))
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        paymentRepository.deleteAll();
        paymentRepository.save(payment);
    }

    @AfterEach
    void cleanAfterEach(){
        paymentRepository.deleteAll();
    }


   // @Test
    void shouldCallbackPaymentSuccessfully() throws Exception {
        PaymentWebhookDTO webhookDTO = PaymentWebhookDTO.builder()
                .paymentId(payment.getId().toString())
                .paymentKey("L90384759")
                .notes("Payment Successfully")
                .status(PaymentStatus.AUTHORIZED)
                .build();

        assertTrue(paymentRepository.findById(payment.getId()).isPresent());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNull(payment.getPaymentCode());

        mockMvc.perform(post("/api/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webhookDTO)))
                .andExpect(status().isNoContent())
                .andReturn();

        Payment paymentResult = paymentRepository.findById(payment.getId()).get();

        assertEquals(PaymentStatus.AUTHORIZED, paymentResult.getStatus());
        assertNotNull(paymentResult.getPaymentCode());
    }

}
