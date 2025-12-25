package com.deliverysistem.integration;

import com.customers.CustomersServiceApplication;
import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.mapper.CustomerMapper;
import com.customers.model.Customer;
import com.customers.model.enums.AuditStatus;
import com.customers.repository.CustomerRepository;
import com.customers.security.TokenValidator;
import com.customers.service.CustomerService;
import com.customers.validator.CustomerValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CustomersServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CustomerProfileControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @MockBean
    private CustomerValidator customerValidator;

    private Customer customer;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("KEYCLOAK_JWK_URI", () -> "http://localhost:8081/realms/master/protocol/openid-connect/certs");
        registry.add("EUREKA_ZONE_URL", () -> "http://localhost:8761/eureka/");
        registry.add("DELIVERIES_RABBITMQ_SUBSCRIBE_PAYMENT_QUEUE", () -> "payments-queue");
        registry.add("DELIVERIES_RABBITMQ_PUBLISHER_DELIVERY_READY", () -> "delivery-ready-queue");
        registry.add("KEYCLOAK_CLIENT_ID", () -> "test-client");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "test-secret");
        registry.add("KEYCLOAK_CLIENT_URL", () -> "http://localhost:8081");

        registry.add("REDIS_HOST", () -> "localhost");
        registry.add("REDIS_PORT", () -> "6379");

        registry.add("CUSTOMERS_DELETED_QUEUE", () -> "customers-deleted-queue");
    }

    @BeforeEach
    void setup() {
        customer = Customer.builder()
                .name("Customer")
                .phone("(99) 9999-9999")
                .email("customer@gmail.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(AuditStatus.ACTIVE)
                .addresses(new ArrayList<>())
                .build();

        customerRepository.deleteAll();
        customerRepository.save(customer);
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldGetCustomerProfileSuccessfully() throws Exception {
        when(customerValidator.resolverAndFindCustomerLogged(any()))
                .thenReturn(customer);

        MvcResult result = mockMvc.perform(get("/api/customers/profile")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.claim("customer_id", customer.getId().toString()))
                        )
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        CustomerResponseDTO customerResponse = objectMapper.readValue(json, CustomerResponseDTO.class);

        assertTrue(customerRepository.findById(customerResponse.id()).isPresent());

        assertNotNull(customerResponse);
        assertEquals(customer.getId(), customerResponse.id());
        assertEquals(customer.getName(), customerResponse.name());
        assertEquals(customer.getEmail(), customerResponse.email());
    }
}