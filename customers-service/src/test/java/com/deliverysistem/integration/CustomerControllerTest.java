package com.deliverysistem.integration;

import com.customers.CustomersServiceApplication;
import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.mapper.CustomerMapper;
import com.customers.repository.CustomerRepository;
import com.customers.security.TokenValidator;
import com.customers.service.CustomerService;
import com.deliverysistem.utils.TestUtils;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CustomersServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(CustomerControllerTest.TestConfig.class)
class CustomerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CustomerService customerService;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private CustomerMapper customerMapper;
    @Autowired private TokenValidator tokenValidator;

    @BeforeEach
    void cleanBeforeEach() {
        customerRepository.deleteAll();
    }

    @AfterEach
    void cleanAfterEach() {
        customerRepository.deleteAll();
    }

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

    @TestConfiguration
    static class TestConfig {

        @Bean
        TokenValidator tokenValidator() {
            return mock(TokenValidator.class);
        }
    }

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        CustomerRequestDTO requestDTO = TestUtils.customerRequestDTO();

        when(tokenValidator.isInternalService(any(Authentication.class)))
                .thenReturn(true);

        MvcResult result = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        CustomerResponseDTO customerResponse = objectMapper.readValue(json, CustomerResponseDTO.class);

        assertTrue(customerRepository.findById(customerResponse.id()).isPresent());

        assertAll(
                () -> assertNotNull(customerResponse.id()),
                () -> assertEquals(requestDTO.name(), customerResponse.name()),
                () -> assertEquals(requestDTO.email(), customerResponse.email()),
                () -> assertEquals(requestDTO.phone(), customerResponse.phone())
        );

        assertEquals(1, customerRepository.count());
    }
}
