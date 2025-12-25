package com.deliverysystem.restaurants.integration;

import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Address;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.enums.AuditStatus;
import com.deliverysystem.restaurants.model.enums.RestaurantStatus;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.service.RestaurantService;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class RestaurantProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RestaurantRepository restaurantRepository;
    @MockBean private RestaurantValidator restaurantValidator;
    private Restaurant restaurant;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("RESTAURANT_DELETED_QUEUE", () -> "restaurant-deleted-queue");
        registry.add("KEYCLOAK_JWK_URI", () -> "http://localhost:8081/realms/master/protocol/openid-connect/certs");
        registry.add("KEYCLOAK_REALM", () -> "master");
        registry.add("KEYCLOAK_CLIENT_ID", () -> "admin-cli");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "dummy");
    }

    @BeforeEach
    void setup() {
        restaurantRepository.deleteAll();

        restaurant = Restaurant.builder()
                .name("Pizza Plaza")
                .email("pizzaplaza@gmail.com")
                .description("Pizza restaurant")
                .website("pizzaplaza.com")
                .status(RestaurantStatus.OPEN)
                .auditStatus(AuditStatus.ACTIVE)
                .address(new Address(
                        "5th Revenue",
                        "23LP",
                        "X89K098K",
                        "NY",
                        "NY",
                        "NY",
                        "USA"
                ))
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        restaurantRepository.saveAndFlush(restaurant);
    }

    @AfterEach
    void cleanAfterTest(){
        restaurantRepository.deleteAll();
    }

    @Test
    void shouldReturnMyRestaurantProfileSuccessfully() throws Exception {
        when(restaurantValidator.getRestaurantIdLogged(any())).thenReturn(restaurant.getId());

        MvcResult result = mockMvc.perform(get("/api/restaurants/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurant.getId().toString()))
                .andExpect(jsonPath("$.name").value("Pizza Plaza"))
                .andExpect(jsonPath("$.email").value("pizzaplaza@gmail.com"))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        RestaurantResponseDTO response = objectMapper.readValue(json, RestaurantResponseDTO.class);

        assertTrue(restaurantRepository.findById(restaurant.getId()).isPresent());

        assertAll(
                () -> assertEquals(restaurant.getId(), response.id()),
                () -> assertNotNull(response)
        );
    }
}
