package com.deliverysystem.integration;

import com.deliverysystem.delivery.client.ClientApiService;
import com.deliverysystem.delivery.client.representation.OrderDTO;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.model.Delivery;
import com.deliverysystem.delivery.model.enums.DeliveryStatus;
import com.deliverysystem.delivery.model.enums.VehicleType;
import com.deliverysystem.delivery.repositories.CurrierRepository;
import com.deliverysystem.delivery.repositories.DeliveryRepository;
import com.deliverysystem.delivery.service.CurrierService;
import com.deliverysystem.delivery.service.DeliveryService;
import com.deliverysystem.delivery.validator.DeliveryValidator;
import com.deliverysystem.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {com.deliverysystem.delivery.DeliveryApplication.class,
        DeliveryControllerTest.TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class DeliveryControllerTest {

    @Autowired private DeliveryService deliveryService;
    @Autowired private DeliveryRepository deliveryRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CurrierRepository currierRepository;
    @Autowired private CurrierService currierService;
    @Autowired private ClientApiService clientApiService;
    @Autowired private DeliveryValidator deliveryValidator;

    private Delivery delivery;
    private OrderDTO orderDTO;
    private Currier currier;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("KEYCLOAK_JWK_URI", () -> "http://localhost:8081/realms/master/protocol/openid-connect/certs");
        registry.add("EUREKA_ZONE_URL", () -> "http://localhost:8761/eureka/");
        registry.add("DELIVERIES_RABBITMQ_SUBSCRIBE_PAYMENT_QUEUE", () -> "payments-queue");
        registry.add("DELIVERIES_RABBITMQ_PUBLISHER_DELIVERY_READY", () -> "delivery-ready-queue");
        registry.add("KEYCLOAK_CLIENT_ID", () -> "test-client");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "test-secret");
        registry.add("KEYCLOAK_CLIENT_URL", () -> "http://localhost:8081");
        registry.add("SERVICE_TOKEN_URL", () -> "http://localhost:9090/service-token");
    }

    @BeforeEach
    void setup() {
        deliveryRepository.deleteAll();
        currierRepository.deleteAll();

        orderDTO = TestUtils.orderDTO();
        delivery = Delivery.builder()
                .orderId(orderDTO.id())
                .totalOrderAmount(orderDTO.total())
                .status(DeliveryStatus.ASSIGNED)
                .deliveryAddress(orderDTO.customer().deliveryAddress())
                .estimatedDeliveryTime(orderDTO.estimated_delivery())
                .build();

        currier = TestUtils.currier();

        deliveryRepository.save(delivery);
        currierRepository.save(currier);

        when(clientApiService.findById(orderDTO.id())).thenReturn(orderDTO);
        when(currierService.findAvailableCourierForDelivery()).thenReturn(currier);
        doNothing().when(deliveryValidator).validateAuthenticatedRestaurantOwnership(orderDTO.restaurantId());
    }


    @AfterEach
    void cleanup() {
        deliveryRepository.deleteAll();
        currierRepository.deleteAll();
    }

    @Test
    void shouldCallbackDeliveryReadySuccessfully() throws Exception {
        UUID deliveryId = delivery.getId();

        mockMvc.perform(post("/api/deliveries/webhook/" + deliveryId))
                .andExpect(status().isNoContent());

        Delivery result = deliveryRepository.findById(deliveryId).orElseThrow();

        assertAll(
                () -> assertEquals(DeliveryStatus.OUT_FOR_DELIVERY, result.getStatus()),
                () -> assertNotNull(result.getCurrier()),
                () -> assertNotNull(result.getDeliveryTax()),
                () -> assertNotNull(result.getActualDeliveryTime())
        );

        verify(clientApiService, times(1)).findById(orderDTO.id());
        verify(deliveryValidator, times(1)).validateAuthenticatedRestaurantOwnership(orderDTO.restaurantId());
        verify(currierService, times(1)).findAvailableCourierForDelivery();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ClientApiService clientApiService() {
            return mock(ClientApiService.class);
        }

        @Bean
        public DeliveryValidator deliveryValidator() {
            return mock(DeliveryValidator.class);
        }

        @Bean
        public CurrierService currierService() {
            return mock(CurrierService.class);
        }
    }
}
