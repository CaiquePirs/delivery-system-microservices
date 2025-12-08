package com.deliverysystem.orders.service;

import com.deliverysystem.orders.client.representation.MenuDTO;
import com.deliverysystem.orders.client.representation.RestaurantDTO;
import com.deliverysystem.orders.client.service.ApiClientService;
import com.deliverysystem.orders.controller.dto.ItemOrderRequestDTO;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemsOrderServiceTest {

    @Mock private ApiClientService apiClientService;
    @InjectMocks private ItemOrderService itemOrderService;

    @Test
    void shouldCreateItemsOrderSuccessfully(){
        RestaurantDTO restaurantDTO = TestUtils.mockRestaurant();
        UUID menuId = restaurantDTO.menus().getFirst().id();

        List<ItemOrderRequestDTO> itemsRequestList = List.of(
                new ItemOrderRequestDTO(1, menuId),
                new ItemOrderRequestDTO(1, menuId)
        );

        List<ItemsOrder> results = assertDoesNotThrow(
                () -> itemOrderService.createItemsOrder(restaurantDTO, itemsRequestList)
        );

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(item -> item.getMenuId().equals(menuId)));

        verify(apiClientService, never()).findMenuById(menuId, restaurantDTO.id());
    }

    @Test
    void shouldThrowExceptionWhenMenuIdIsNotFound(){
        RestaurantDTO restaurantDTO = new RestaurantDTO(
                UUID.randomUUID(),
                "Italian Food",
                "italianfood@gmail.com",
                "italianfood.com",
                "Italian Food Restaurant",
                "OPEN",
                TestUtils.mockAddress(),
                List.of(new MenuDTO(UUID.randomUUID(), "", BigDecimal.valueOf(10.00), "FOOD"))
        );

        UUID menu_id_01 = UUID.randomUUID();

        List<ItemOrderRequestDTO> itemsRequestList = List.of(
                new ItemOrderRequestDTO(1, menu_id_01)
        );

        String messageException = String.format("Menu ID: %s not found", menu_id_01);

        doThrow(new ClientNotFoundException(messageException))
                .when(apiClientService).findMenuById(menu_id_01, restaurantDTO.id());

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> itemOrderService.createItemsOrder(restaurantDTO, itemsRequestList)
        );

        assertEquals(messageException, ex.getMessage());

        verify(apiClientService, times(1)).findMenuById(menu_id_01, restaurantDTO.id());
    }



}
