package com.deliverysystem.restaurants;

import com.deliverysystem.restaurants.controller.advice.exceptions.MenuNotFoundException;
import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantNotFoundException;
import com.deliverysystem.restaurants.controller.dto.MenuRequestDTO;
import com.deliverysystem.restaurants.mapper.MenuMapper;
import com.deliverysystem.restaurants.model.Menu;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.enums.AuditStatus;
import com.deliverysystem.restaurants.model.enums.MenuStatus;
import com.deliverysystem.restaurants.model.enums.MenuType;
import com.deliverysystem.restaurants.repository.MenuRepository;
import com.deliverysystem.restaurants.service.MenuService;
import com.deliverysystem.restaurants.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock private MenuRepository menuRepository;
    @Mock private RestaurantService restaurantService;
    @Mock private MenuMapper menuMapper;
    @InjectMocks private MenuService menuService;

    private UUID restaurantId;
    private UUID menuId;
    private Menu menu;
    private Restaurant restaurant;

    @BeforeEach
    void setUp(){
        restaurantId = UUID.randomUUID();
        menuId = UUID.randomUUID();

        restaurant = Restaurant.builder()
                .id(restaurantId)
                .menus(new ArrayList<>())
                .build();

        menu = Menu.builder()
                .id(menuId)
                .auditStatus(AuditStatus.ACTIVE)
                .menuType(MenuType.DINNER)
                .price(BigDecimal.valueOf(20.00))
                .updated_at(LocalDateTime.now())
                .created_at(LocalDateTime.now())
                .description("Sushi")
                .restaurant(restaurant)
                .status(MenuStatus.AVAILABLE)
                .build();
    }

    @Test
    void shouldCreateMenuSuccessfully(){
        MenuRequestDTO dto = new MenuRequestDTO(
                "Sushi",
                MenuType.DINNER,
                BigDecimal.valueOf(40.00)
        );

        when(restaurantService.findRestaurantById(restaurantId)).thenReturn(restaurant);
        when(menuRepository.save(menu)).thenReturn(menu);
        when(menuMapper.toEntity(dto)).thenReturn(menu);

        Menu menuResult = assertDoesNotThrow(() -> menuService.createMenu(restaurantId, dto));

        assertAll(
                () -> assertEquals(restaurantId, menuResult.getRestaurant().getId()),
                () -> assertNotNull(menuResult.getId()),
                () -> assertTrue(restaurant.getMenus().contains(menuResult)),
                () -> assertEquals(MenuStatus.AVAILABLE, menuResult.getStatus()),
                () -> assertEquals(AuditStatus.ACTIVE, menuResult.getAuditStatus())
        );

        verify(restaurantService, times(1)).findRestaurantById(restaurantId);
        verify(menuRepository, times(1)).save(menu);
        verify(menuMapper, times(1)).toEntity(dto);
    }

    @Test
    void shouldFindAvailableMenuById(){
        restaurant.getMenus().add(menu);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(restaurantService.findRestaurantById(restaurantId)).thenReturn(restaurant);

        Menu menuResult = assertDoesNotThrow(() -> menuService.findAvailableMenuById(restaurantId, menuId));

        assertNotNull(menuResult);
        assertEquals(menuId, menuResult.getId());
        assertEquals(restaurantId, menuResult.getRestaurant().getId());
        assertEquals(MenuStatus.AVAILABLE, menuResult.getStatus());
        assertEquals(AuditStatus.ACTIVE, menuResult.getAuditStatus());

        verify(menuRepository, times(1)).findById(menuId);
        verify(restaurantService, times(1)).findRestaurantById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenMenuIsUnavailable(){
        menu.setStatus(MenuStatus.UNAVAILABLE);

        String messageException = String.format("Menu ID: %s not found", menuId);

        MenuNotFoundException ex = assertThrows(
                MenuNotFoundException.class,
                () -> menuService.findAvailableMenuById(restaurantId, menuId)
        );
        assertEquals(messageException, ex.getMessage());
        assertEquals(MenuStatus.UNAVAILABLE, menu.getStatus());

        verify(menuRepository, times(1)).findById(menuId);
        verify(restaurantService, never()).findRestaurantById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenMenuIsDeleted(){
        menu.setAuditStatus(AuditStatus.DELETED);

        String messageException = String.format("Menu ID: %s not found", menuId);

        MenuNotFoundException ex = assertThrows(
                MenuNotFoundException.class,
                () -> menuService.findAvailableMenuById(restaurantId, menuId)
        );
        assertEquals(messageException, ex.getMessage());
        assertEquals(AuditStatus.DELETED, menu.getAuditStatus());

        verify(menuRepository, times(1)).findById(menuId);
        verify(restaurantService, never()).findRestaurantById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenMenuAvailableIdIsNotFound(){
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        String messageException = String.format("Menu ID: %s not found", menuId);

        MenuNotFoundException ex = assertThrows(
                MenuNotFoundException.class,
                () -> menuService.findAvailableMenuById(restaurantId, menuId)
        );
        assertEquals(messageException, ex.getMessage());

        verify(menuRepository, times(1)).findById(menuId);
        verify(restaurantService, never()).findRestaurantById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIsNotFound(){
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        String messageException = String.format("Restaurant ID: %s not found", restaurantId);

        doThrow(new RestaurantNotFoundException(messageException))
                .when(restaurantService).findRestaurantById(restaurantId);

        RestaurantNotFoundException ex = assertThrows(
                RestaurantNotFoundException.class,
                () -> menuService.findAvailableMenuById(restaurantId, menuId)
        );

        assertEquals(messageException, ex.getMessage());

        verify(menuRepository, times(1)).findById(menuId);
        verify(restaurantService, times(1)).findRestaurantById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenFindMenuAvailableAndMenuNotBelongToRestaurant(){
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(restaurantService.findRestaurantById(restaurantId)).thenReturn(restaurant);

        MenuNotFoundException ex = assertThrows(
                MenuNotFoundException.class,
                () -> menuService.findAvailableMenuById(restaurantId, menuId)
        );
        assertEquals("This menu does not belong to this restaurant.", ex.getMessage());
        assertFalse(restaurant.getMenus().contains(menu));

        verify(menuRepository, times(1)).findById(menuId);
        verify(restaurantService, times(1)).findRestaurantById(restaurantId);
    }

    @Test
    void shouldToggleMenuStatusToUnavailableSuccessfully(){
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(menuRepository.save(menu)).thenReturn(menu);

        assertEquals(MenuStatus.AVAILABLE, menu.getStatus());
        assertDoesNotThrow(() -> menuService.toggleMenuStatus(menuId));
        assertEquals(MenuStatus.UNAVAILABLE, menu.getStatus());

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void shouldToggleMenuStatusToAvailableSuccessfully(){
        menu.setStatus(MenuStatus.UNAVAILABLE);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(menuRepository.save(menu)).thenReturn(menu);

        assertEquals(MenuStatus.UNAVAILABLE, menu.getStatus());
        assertDoesNotThrow(() -> menuService.toggleMenuStatus(menuId));
        assertEquals(MenuStatus.AVAILABLE, menu.getStatus());

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void shouldThrowExceptionWhenToggleMenuStatusAndMenuIsNotFound(){
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        String messageException = String.format("Menu ID: %s not found", menuId);

        MenuNotFoundException ex = assertThrows(
                MenuNotFoundException.class,
                () -> menuService.findAvailableMenuById(restaurantId, menuId)
        );
        assertEquals(messageException, ex.getMessage());

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).save(menu);
    }

    @Test
    void shouldDisableMenuByIdSuccessfully(){
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        assertEquals(AuditStatus.ACTIVE, menu.getAuditStatus());

        assertDoesNotThrow(() -> menuService.disableMenuById(menuId));
        assertEquals(AuditStatus.DELETED, menu.getAuditStatus());

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void shouldThrowExceptionWhenDisableMenuAndMenuIsNotFound() {
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        String messageException = String.format("Menu ID: %s not found", menuId);

        MenuNotFoundException ex = assertThrows(
                MenuNotFoundException.class,
                () -> menuService.disableMenuById(menuId)
        );
        assertEquals(messageException, ex.getMessage());

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).save(menu);
    }
}
