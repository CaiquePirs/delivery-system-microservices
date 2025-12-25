package com.systemdelivery.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import com.systemdelivery.authentication.service.AuthenticationService;
import com.systemdelivery.authentication.service.RedisService;
import com.systemdelivery.authentication.service.keycloakService;
import com.systemdelivery.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {com.systemdelivery.authentication.AuthenticationApplicationTest.class,
        AuthenticationControllerTest.TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;
    @Autowired private RedisService redisService;
    @Autowired private keycloakService keycloakService;

    @DynamicPropertySource
    static void registerMissingProperties(DynamicPropertyRegistry registry) {
        registry.add("KEYCLOAK_CLIENT_ID", () -> "test-client-id");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "test-client-secret");
        registry.add("KEYCLOAK_TOKEN_URL", () -> "http://localhost:9999/token");
        registry.add("KEYCLOAK_REALM", () -> "test-realm");
        registry.add("KEYCLOAK_SERVER_URL", () -> "http://localhost:9999/server");
        registry.add("CUSTOMERS_DELETED_QUEUE", () -> "CUSTOMER_DELETED_QUEUE");
        registry.add("RESTAURANT_DELETED_QUEUE", () -> "RESTAURANT_DELETED_QUEUE");
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public RedisService redisService() {
            return mock(RedisService.class);
        }

        @Bean
        @Primary
        public keycloakService keycloakService() {
            return mock(keycloakService.class);
        }
    }

    @Test
    void shouldLoginSuccessfullyWhenTokenIsNotInRedis() throws Exception {
        LoginRequestDTO loginRequest = TestUtils.loginRequestDTO();
        LoginResponseDTO loginResponse = TestUtils.loginResponseDTO();

        when(redisService.findUserTokenInCache(loginRequest.email())).thenReturn(null);
        when(keycloakService.loginInKeycloak(loginRequest)).thenReturn(loginResponse);
        doNothing().when(redisService).insertUserTokenInCache(loginRequest.email(), loginResponse);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO responseResult = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                LoginResponseDTO.class
        );

        assertNotNull(loginRequest.email());
        assertNotNull(loginRequest.password());

        assertNotNull(loginResponse.tokenType());
        assertNotNull(loginResponse.accessToken());
        assertNotNull(responseResult);

        verify(redisService, times(1)).findUserTokenInCache(loginRequest.email());
        verify(redisService, times(1)).insertUserTokenInCache(loginRequest.email(), loginResponse);
    }

    @Test
    void shouldLoginSuccessfullyWhenTokenIsInRedis() throws Exception{
        LoginRequestDTO loginRequest = TestUtils.loginRequestDTO();
        LoginResponseDTO loginResponse = TestUtils.loginResponseDTO();

        when(redisService.findUserTokenInCache(loginRequest.email())).thenReturn(loginResponse);
        doNothing().when(redisService).insertUserTokenInCache(loginRequest.email(), loginResponse);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        LoginResponseDTO loginResponseResult = objectMapper.readValue(json, LoginResponseDTO.class);

        assertNotNull(loginRequest.email());
        assertNotNull(loginRequest.password());

        assertNotNull(loginResponseResult.tokenType());
        assertNotNull(loginResponseResult.accessToken());

        verify(keycloakService, never()).loginInKeycloak(loginRequest);
        verify(redisService, times(1)).findUserTokenInCache(loginRequest.email());
        verify(redisService, never()).insertUserTokenInCache(loginRequest.email(), loginResponse);
    }


}
