package com.systemdelivery.authentication.client;

import com.systemdelivery.authentication.client.api.ApiCustomerClient;
import com.systemdelivery.authentication.client.api.ApiRestaurantClient;
import com.systemdelivery.authentication.client.service.ApiClientService;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.*;
import com.systemdelivery.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @InjectMocks private ApiClientService apiClientService;
    @Mock private ApiRestaurantClient apiRestaurantClient;
    @Mock private ApiCustomerClient apiCustomerClient;

    private CreateAddressRequestDTO createAddressRequestDTO;
    private CreateCustomerRequestDTO createCustomerRequestDTO;
    private CreateRestaurantRequestDTO createRestaurantRequestDTO;

    @BeforeEach
    void setUp(){
        createAddressRequestDTO = TestUtils.createAddressRequestDTO();
        createCustomerRequestDTO = TestUtils.createCustomerRequestDTO();
        createRestaurantRequestDTO = TestUtils.createRestaurantRequestDTO();
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomersSuccessfully() {
        ResponseEntity<CustomerResponseDTO> responseEntity = ResponseEntity
                .status(HttpStatus.CREATED).body(TestUtils.customerResponseDTO());

        when(apiCustomerClient.createCustomer(createCustomerRequestDTO))
                .thenReturn(responseEntity);

        CustomerResponseDTO result = apiClientService.createCustomer(createCustomerRequestDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertAll(
                () -> {
                    assertNotNull(result);
                    assertNotNull(result.id());
                    assertEquals(createCustomerRequestDTO.name(), result.name());
                    assertEquals(createCustomerRequestDTO.email(), result.email());
                }
        );
        verify(apiCustomerClient, times(1)).createCustomer(createCustomerRequestDTO);
    }

    @Test
    @DisplayName("Should create restaurant successfully")
    void shouldCreateRestaurantSuccessfully() {
        ResponseEntity<RestaurantResponseDTO> responseEntity = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TestUtils.createRestaurant());

        when(apiRestaurantClient.createRestaurant(createRestaurantRequestDTO))
                .thenReturn(responseEntity);

        RestaurantResponseDTO result = apiClientService.createRestaurant(createRestaurantRequestDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertAll(
                () -> {
                    assertNotNull(result);
                    assertNotNull(result.id());
                    assertEquals(createRestaurantRequestDTO.name(), result.name());
                    assertEquals(createRestaurantRequestDTO.email(), result.email());
                }
        );
        verify(apiRestaurantClient, times(1)).createRestaurant(createRestaurantRequestDTO);
    }

    @Test
    void shouldThrowExceptionWhenCustomerAlreadyExists() {
        ResponseEntity<CustomerResponseDTO> responseEntity = ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(null);

        when(apiCustomerClient.createCustomer(createCustomerRequestDTO))
                .thenReturn(responseEntity);

        String messageException = "This email already exists: " + createCustomerRequestDTO.email();

        ErrorRegisterException ex = assertThrows(
                ErrorRegisterException.class,
                () -> apiClientService.createCustomer(createCustomerRequestDTO)
        );

        assertEquals(messageException, ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        verify(apiCustomerClient, times(1)).createCustomer(createCustomerRequestDTO);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantAlreadyExists() {
        ResponseEntity<RestaurantResponseDTO> responseEntity = ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(null);

        when(apiRestaurantClient.createRestaurant(createRestaurantRequestDTO))
                .thenReturn(responseEntity);

        String messageException = "This email already exists: " + createRestaurantRequestDTO.email();

        ErrorRegisterException ex = assertThrows(
                ErrorRegisterException.class,
                () -> apiClientService.createRestaurant(createRestaurantRequestDTO)
        );

        assertEquals(messageException, ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        verify(apiRestaurantClient, times(1)).createRestaurant(createRestaurantRequestDTO);
    }

    @Test
    @DisplayName("Should throw an exception when attempting to register a customer and return an HTTP status of invalid request.")
    void shouldThrowExceptionWhenTryingToRegisterACustomer(){
        ResponseEntity<CustomerResponseDTO> responseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(null);

        when(apiCustomerClient.createCustomer(createCustomerRequestDTO)).thenReturn(responseEntity);
        String messageException = "Error when registering customer with status: " + responseEntity.getStatusCode();

        ErrorRegisterException ex = assertThrows(
                ErrorRegisterException.class,
                () -> apiClientService.createCustomer(createCustomerRequestDTO)
        );

        assertEquals(messageException, ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        verify(apiCustomerClient, times(1)).createCustomer(createCustomerRequestDTO);
    }

    @Test
    @DisplayName("Should throw exception when attempting to register a restaurant and return an HTTP status of invalid request.")
    void shouldThrowExceptionWhenTryingToRegisterARestaurant(){
        ResponseEntity<RestaurantResponseDTO> responseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(null);

        when(apiRestaurantClient.createRestaurant(createRestaurantRequestDTO)).thenReturn(responseEntity);
        String messageException = "Error when registering restaurant with status: " + responseEntity.getStatusCode();

        ErrorRegisterException ex = assertThrows(
                ErrorRegisterException.class,
                () -> apiClientService.createRestaurant(createRestaurantRequestDTO)
        );

        assertEquals(messageException, ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        verify(apiRestaurantClient, times(1)).createRestaurant(createRestaurantRequestDTO);
    }

    @Test
    void shouldDeletingCustomerSuccessfully() {
        UUID customerId = UUID.randomUUID();

        ResponseEntity<Void> responseEntity = ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

        when(apiCustomerClient.deleteCustomer(customerId)).thenReturn(responseEntity);

        assertDoesNotThrow(() -> apiClientService.deleteCustomerById(customerId));
        assertEquals(HttpStatus.NO_CONTENT,  responseEntity.getStatusCode());
    }

    @Test
    void shouldDeleteRestaurantSuccessfully() {
        UUID restaurantId = UUID.randomUUID();

        ResponseEntity<Void> responseEntity = ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

        when(apiRestaurantClient.deleteRestaurant(restaurantId)).thenReturn(responseEntity);

        assertDoesNotThrow(() -> apiClientService.deleteRestaurantById(restaurantId));
        assertEquals(HttpStatus.NO_CONTENT,  responseEntity.getStatusCode());
    }
}
