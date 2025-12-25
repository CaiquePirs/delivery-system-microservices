package com.deliverysystem.restaurants.integration;

import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Address;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.enums.AuditStatus;
import com.deliverysystem.restaurants.model.enums.RestaurantStatus;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.security.TokenValidator;
import com.deliverysystem.restaurants.service.RedisService;
import com.deliverysystem.restaurants.service.RestaurantService;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RestaurantControllerTest {

    @Autowired private RestaurantService restaurantService;
    @Autowired private RestaurantRepository restaurantRepository;
    @MockBean private TokenValidator tokenValidator;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RestaurantMapper restaurantMapper;
    @Autowired private MockMvc mockMvc;
    @MockBean private RedisService redisService;

    private RestaurantRequestDTO restaurantRequest;
    private Restaurant restaurant;
    private Address address;
    private Authentication authentication;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("RESTAURANT_DELETED_QUEUE", () -> "restaurant-deleted-queue");
        registry.add("KEYCLOAK_JWK_URI", () -> "http://localhost:8081/realms/master/protocol/openid-connect/certs");
        registry.add("KEYCLOAK_REALM", () -> "master");
        registry.add("KEYCLOAK_CLIENT_ID", () -> "admin-cli");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "dummy");
    }

    @BeforeEach
    void cleanBeforeEach(){
        address = new Address(
                "5th Revenue",
                "23LP",
                "X89K098K",
                "NY",
                "NY",
                "NY",
                "USA"
        );

        restaurantRequest = new RestaurantRequestDTO(
                "pizzaplaza@gmail.com",
                "Pizza Plaza",
                "pizzaplaza.com.br",
                "Deliciuos Pizzas",
                address
        );

        restaurant = Restaurant.builder()
                .id(UUID.randomUUID())
                .name(restaurantRequest.name())
                .email(restaurantRequest.email())
                .address(address)
                .status(RestaurantStatus.OPEN)
                .auditStatus(AuditStatus.ACTIVE)
                .menus(new ArrayList<>())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        restaurantRepository.deleteAll();
    }

    @AfterEach
    void cleanAfterEach(){
        restaurantRepository.deleteAll();
    }

    @Test
    void shouldCreateRestaurantSuccessfully() throws Exception {
        when(tokenValidator.isInternalService(authentication)).thenReturn(true);

        MvcResult result = mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        RestaurantResponseDTO responseResult = objectMapper.readValue(json, RestaurantResponseDTO.class);

        assertTrue(restaurantRepository.findById(responseResult.id()).isPresent());

        assertAll(
                () -> assertNotNull(responseResult),
                () -> assertNotNull(responseResult.id()),
                () -> assertEquals(responseResult.email(), restaurantRequest.email()),
                () -> assertEquals(responseResult.name(), restaurantRequest.name()),
                () -> assertEquals(responseResult.description(), restaurantRequest.description()),
                () -> assertEquals(responseResult.website(), restaurantRequest.website())
        );
    }


}
