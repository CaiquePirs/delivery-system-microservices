package com.deliverysystem.restaurants.integration;

import com.deliverysystem.restaurants.controller.dto.MenuRequestDTO;
import com.deliverysystem.restaurants.controller.dto.MenuResponseDTO;
import com.deliverysystem.restaurants.mapper.MenuMapper;
import com.deliverysystem.restaurants.model.Address;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.enums.AuditStatus;
import com.deliverysystem.restaurants.model.enums.MenuType;
import com.deliverysystem.restaurants.model.enums.RestaurantStatus;
import com.deliverysystem.restaurants.repository.MenuRepository;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.security.TokenValidator;
import com.deliverysystem.restaurants.service.MenuService;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class MenuControllerTest {

    @Autowired private MenuService menuService;
    @Autowired private MenuMapper menuMapper;
    @Autowired private MenuRepository menuRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @MockBean private RestaurantValidator restaurantValidator;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private TokenValidator tokenValidator;

    private Restaurant restaurant;
    private Address address;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("RESTAURANT_DELETED_QUEUE", () -> "restaurant-deleted-queue");
        registry.add("KEYCLOAK_JWK_URI", () -> "http://localhost:8081/realms/master/protocol/openid-connect/certs");
        registry.add("KEYCLOAK_REALM", () -> "master");
        registry.add("KEYCLOAK_CLIENT_ID", () -> "admin-cli");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "dummy");
    }

    @AfterEach
    void cleanAfterEach(){
        menuRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        menuRepository.deleteAll();
        restaurantRepository.deleteAll();

        address = new Address(
                "5th Revenue",
                "23LP",
                "X89K098K",
                "NY",
                "NY",
                "NY",
                "USA"
        );

        restaurant = Restaurant.builder()
                .name("Pizza Plaza")
                .email("pizzaplaza@gmail.com")
                .description("Pizza restaurant")
                .website("pizza.com.br")
                .address(address)
                .status(RestaurantStatus.OPEN)
                .auditStatus(AuditStatus.ACTIVE)
                .menus(new ArrayList<>())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        restaurantRepository.saveAndFlush(restaurant);
    }

    @Test
    void shouldCreateMenuSuccessfully() throws Exception {
        doNothing().when(restaurantValidator).validateIfIsSameRestaurant(
                eq(restaurant.getId()), any(Jwt.class)
        );

        MenuRequestDTO dto = new MenuRequestDTO(
                "Pizza Chaser",
                MenuType.DINNER,
                BigDecimal.valueOf(35.00)
        );

        MvcResult result = mockMvc.perform(post("/api/restaurants/" + restaurant.getId() + "/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        MenuResponseDTO responseResult = objectMapper.readValue(json, MenuResponseDTO.class);

        assertTrue(restaurantRepository.findById(restaurant.getId()).isPresent());
        assertTrue(menuRepository.findById(responseResult.id()).isPresent());

        assertAll(
                () -> assertNotNull(responseResult.id()),
                () -> assertEquals(dto.menuType(), responseResult.menuType()),
                () -> assertEquals(dto.price(), responseResult.price())
        );
    }
}
