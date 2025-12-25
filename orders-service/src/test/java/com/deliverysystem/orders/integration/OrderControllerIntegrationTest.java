package com.deliverysystem.orders.integration;

import com.deliverysystem.orders.client.representation.CustomerDTO;
import com.deliverysystem.orders.client.representation.MenuDTO;
import com.deliverysystem.orders.client.representation.RestaurantDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.ItemOrderRequestDTO;
import com.deliverysystem.orders.controller.dto.OrderRequestDTO;
import com.deliverysystem.orders.event.publisher.OrderEventPublisher;
import com.deliverysystem.orders.mapper.OrderMapper;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.model.PaymentData;
import com.deliverysystem.orders.model.enums.OrderStatus;
import com.deliverysystem.orders.model.enums.PaymentMethod;
import com.deliverysystem.orders.repository.OrderRepository;
import com.deliverysystem.orders.service.ItemOrderService;
import com.deliverysystem.orders.service.OrderService;
import com.deliverysystem.orders.service.calculator.OrderCalculator;
import com.deliverysystem.orders.service.validator.AccessValidator;
import com.deliverysystem.orders.service.validator.OrderValidator;
import com.deliverysystem.orders.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ItemOrderService itemOrderService;
    @Autowired private OrderCalculator orderCalculator;
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderEventPublisher eventPublisher;
    @Autowired private OrderService orderService;

    @MockBean private ApiClientService apiClientService;
    @MockBean private AccessValidator accessValidator;
    @MockBean private OrderValidator orderValidator;

    private CustomerDTO customer;
    private RestaurantDTO restaurant;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("KEYCLOAK_JWK_URI",
                () -> "http://localhost:8081/realms/master/protocol/openid-connect/certs");
        registry.add("KEYCLOAK_REALM", () -> "master");
        registry.add("KEYCLOAK_CLIENT_ID", () -> "admin-cli");
        registry.add("KEYCLOAK_CLIENT_SECRET", () -> "dummy");
        registry.add("SERVICE_TOKEN_URL",
                () -> "http://localhost:8082/mock-token");
    }

    @BeforeEach
    void setup() {
        customer = TestUtils.mockCustomer();
        restaurant = TestUtils.mockRestaurant();

        orderRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        OrderRequestDTO orderRequest = new OrderRequestDTO(
                customer.address().get(0).id(),
                restaurant.id(),
                "No pickles",
                new PaymentData("938495839", PaymentMethod.CARD),
                List.of(new ItemOrderRequestDTO(2, restaurant.menus().getFirst().id()))
        );

        when(accessValidator.getCustomerIdLogged()).thenReturn(customer.id());
        when(apiClientService.findCustomerById(customer.id())).thenReturn(CompletableFuture.completedFuture(customer));
        when(apiClientService.findRestaurantById(orderRequest.restaurantId())).thenReturn(CompletableFuture.completedFuture(restaurant));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        List<Order> orders = orderRepository.findAll();
        Order orderSaved = orders.get(0);

        assertAll(
                () -> assertTrue(orderRepository.findById(orderSaved.getId()).isPresent()),
                () -> assertEquals("OPEN", restaurant.status()),
                () -> assertEquals(BigDecimal.valueOf(90.00), orderSaved.getTotal()),
                () -> assertEquals(OrderStatus.PENDING_PAYMENT, orderSaved.getStatus()),
                () -> assertNotNull(orderSaved.getCreated_at()),
                () -> assertNotNull(orderSaved.getUpdated_at()),
                () -> assertNotNull(orderSaved.getEstimatedDelivery())
        );
    }
}
