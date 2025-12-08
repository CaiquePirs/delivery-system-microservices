package com.deliverysystem.orders.validator;

import com.deliverysystem.orders.client.representation.CustomerDTO;
import com.deliverysystem.orders.client.representation.DeliveryAddressDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import com.deliverysystem.orders.controller.exception.RestaurantClosedException;
import com.deliverysystem.orders.service.validator.OrderValidator;
import com.deliverysystem.orders.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class OrderValidatorTest {

    @Mock private ApiClientService apiClientService;
    @InjectMocks private OrderValidator orderValidator;

    @Test
    void shouldResolveDeliveryAddressSuccessfully(){
        CustomerDTO customer = TestUtils.mockCustomer();
        UUID deliveryAddressId = customer.address().stream().findFirst().get().id();

        DeliveryAddressDTO result = assertDoesNotThrow(
                () -> orderValidator.resolveDeliveryAddress(deliveryAddressId, customer)
        );

        assertNotNull(result);
        assertNotNull(deliveryAddressId);
        assertTrue(customer.address().contains(result));
        assertTrue(customer.address().stream().anyMatch(address -> address.id().equals(deliveryAddressId)));

        verify(apiClientService, never()).findAddressById(deliveryAddressId);
    }

    @Test
    void shouldThrowExceptionWhenDeliveryAddressIdIsNotFound(){
        CustomerDTO customer = TestUtils.mockCustomer();
        UUID deliveryAddressId = UUID.randomUUID();

        String messageException = String.format("Address ID: %s not found", deliveryAddressId);
        doThrow(new ClientNotFoundException(messageException))
                .when(apiClientService).findAddressById(deliveryAddressId);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> orderValidator.resolveDeliveryAddress(deliveryAddressId, customer)
        );

        assertEquals(messageException, ex.getMessage());
        assertFalse(customer.address().stream().anyMatch(address -> address.id().equals(deliveryAddressId)));
    }

    @Test
    void shouldValidateIfRestaurantIsOpenSuccessfully(){
        String restaurantStatus = "OPEN";
        assertDoesNotThrow(() -> orderValidator.validateIfRestaurantIsOpen(restaurantStatus));
    }

    @Test
    void shouldThrowExceptionWhenRestaurantStatusIsClosed(){
        String restaurantStatus = "CLOSED";

        RestaurantClosedException ex = assertThrows(
                RestaurantClosedException.class,
                () -> orderValidator.validateIfRestaurantIsOpen(restaurantStatus)
        );
        assertEquals("The selected restaurant is currently closed for orders.", ex.getMessage());
        assertEquals("CLOSED",restaurantStatus);
    }


}
