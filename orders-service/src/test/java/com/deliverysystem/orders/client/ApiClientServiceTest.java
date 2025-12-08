package com.deliverysystem.orders.client;

import com.deliverysystem.orders.client.api.CustomerClientApi;
import com.deliverysystem.orders.client.api.RestaurantClientApi;
import com.deliverysystem.orders.client.representation.CustomerDTO;
import com.deliverysystem.orders.client.representation.DeliveryAddressDTO;
import com.deliverysystem.orders.client.representation.MenuDTO;
import com.deliverysystem.orders.client.representation.RestaurantDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import com.deliverysystem.orders.utils.TestUtils;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ApiClientServiceTest {

    @Mock private CustomerClientApi customerClientApi;
    @Mock private RestaurantClientApi restaurantClientApi;
    @InjectMocks private ApiClientService apiClientService;

    @Test
    void shouldFindCustomerByIdViaApiSuccessfully() throws Exception{
        CustomerDTO mockCustomer = TestUtils.mockCustomer();
        UUID customerId = mockCustomer.id();

        when(customerClientApi.findCustomerById(customerId)).thenReturn(ResponseEntity.ok(mockCustomer));

        CompletableFuture<CustomerDTO> customerFuture = assertDoesNotThrow(
                () -> apiClientService.findCustomerById(customerId)
        );

        CustomerDTO result = customerFuture.resultNow();

        assertNotNull(customerFuture);
        assertNotNull(customerFuture.get());

        assertAll(
                () -> assertEquals(mockCustomer.id(), result.id()),
                () -> assertEquals(mockCustomer.name(), result.name()),
                () -> assertEquals(mockCustomer.email(), result.email()),
                () -> assertEquals(mockCustomer.phone(), result.phone())
        );

        verify(customerClientApi, times(1)).findCustomerById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenErrorOccursWhileTryingRetrieveCustomerByID(){
        UUID customerId = UUID.randomUUID();

        doThrow(FeignException.class).when(customerClientApi).findCustomerById(customerId);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> apiClientService.findCustomerById(customerId)
        );

        assertEquals(String.format("Customer ID: %s not found", customerId), ex.getMessage());

        verify(customerClientApi, times(1)).findCustomerById(customerId);
    }

    @Test
    void shouldFindRestaurantViaApiSuccessfully() throws Exception{
        RestaurantDTO mockRestaurant = TestUtils.mockRestaurant();
        UUID restaurantId = mockRestaurant.id();

        when(restaurantClientApi.findRestaurantById(restaurantId)).thenReturn(ResponseEntity.ok(mockRestaurant));

        CompletableFuture<RestaurantDTO> restaurantFuture = assertDoesNotThrow(
                () -> apiClientService.findRestaurantById(restaurantId)
        );

        RestaurantDTO result = restaurantFuture.resultNow();

        assertNotNull(restaurantFuture);
        assertNotNull(restaurantFuture.get());

        assertAll(
                () -> assertEquals(mockRestaurant.id(), result.id()),
                () -> assertEquals(mockRestaurant.name(), result.name()),
                () -> assertEquals(mockRestaurant.email(), result.email())
        );

        verify(restaurantClientApi, times(1)).findRestaurantById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenErrorOccursWhileTryingRetrieveRestaurantByID(){
        UUID restaurantId = UUID.randomUUID();

        doThrow(FeignException.class).when(restaurantClientApi).findRestaurantById(restaurantId);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> apiClientService.findRestaurantById(restaurantId)
        );

        assertEquals(String.format("Restaurant ID: %s not found", restaurantId), ex.getMessage());

        verify(restaurantClientApi, times(1)).findRestaurantById(restaurantId);
    }

    @Test
    void shouldFindMenuByIdViaApiSuccessfully(){
        MenuDTO mockMenu = TestUtils.menuDTO();
        UUID menuId = mockMenu.id();
        UUID restaurantId = UUID.randomUUID();

        when(restaurantClientApi.findMenuById(menuId, restaurantId))
                .thenReturn(ResponseEntity.ok(mockMenu));

        MenuDTO menuResult = assertDoesNotThrow(
                () -> apiClientService.findMenuById(restaurantId, menuId)
        );

        assertAll(
                () -> assertNotNull(menuResult),
                () -> assertEquals(menuResult.id(), mockMenu.id()),
                () -> assertEquals(menuResult.menuType(), mockMenu.menuType()),
                () -> assertEquals(menuResult.description(), mockMenu.description())
        );

        verify(restaurantClientApi, times(1)).findMenuById(menuId, restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenErrorOccursWhileTryingRetrieveMenuByID(){
        UUID restaurantId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();

        doThrow(FeignException.class).when(restaurantClientApi).findMenuById(restaurantId, menuId);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> apiClientService.findMenuById(menuId, restaurantId)
        );

        assertEquals(String.format("Menu ID: %s not found", menuId), ex.getMessage());
        verify(restaurantClientApi, times(1)).findMenuById(restaurantId, menuId);
    }

    @Test
    void shouldFindDeliveryAddressViaApiSuccessfully(){
        DeliveryAddressDTO mockAddress = TestUtils.mockAddress();
        UUID deliveryAddressId = mockAddress.id();

        when(customerClientApi.findAddressById(deliveryAddressId))
                .thenReturn(ResponseEntity.ok(mockAddress));

        DeliveryAddressDTO result = assertDoesNotThrow(() -> apiClientService.findAddressById(deliveryAddressId));

        assertNotNull(result.id());
        assertNotNull(result.city());
        assertNotNull(result.country());
        assertNotNull(result.neighborhood());
        assertNotNull(result.number());
        assertNotNull(result.street());
        assertNotNull(result.zipcode());

        verify(customerClientApi, times(1)).findAddressById(deliveryAddressId);
    }

    @Test
    void shouldThrowExceptionWhenErrorOccursWhileTryingRetrieveDeliveryAddressByID(){
        UUID deliveryAddressId = UUID.randomUUID();

        doThrow(FeignException.class).when(customerClientApi).findAddressById(deliveryAddressId);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> apiClientService.findAddressById(deliveryAddressId)
        );

        assertEquals(String.format("Address ID: %s not found", deliveryAddressId), ex.getMessage());
        verify(customerClientApi, times(1)).findAddressById(deliveryAddressId);
    }
}
