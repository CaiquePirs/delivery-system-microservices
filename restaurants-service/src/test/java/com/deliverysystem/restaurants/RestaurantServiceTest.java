package com.deliverysystem.restaurants;

import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantFoundException;
import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantNotFoundException;
import com.deliverysystem.restaurants.controller.dto.RestaurantQueryFilter;
import com.deliverysystem.restaurants.controller.dto.RestaurantRequestDTO;
import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.event.publisher.RestaurantEventPublisher;
import com.deliverysystem.restaurants.event.representation.RestaurantDeletedEvent;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Address;
import com.deliverysystem.restaurants.model.Menu;
import com.deliverysystem.restaurants.model.Restaurant;
import com.deliverysystem.restaurants.model.enums.AuditStatus;
import com.deliverysystem.restaurants.model.enums.RestaurantStatus;
import com.deliverysystem.restaurants.repository.RestaurantRepository;
import com.deliverysystem.restaurants.service.RedisService;
import com.deliverysystem.restaurants.service.RestaurantService;
import com.deliverysystem.restaurants.validator.RestaurantValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock private RestaurantRepository repository;
    @Mock private RestaurantValidator validator;
    @Mock private RedisService redisService;
    @Mock private RestaurantMapper mapper;
    @Mock private RestaurantEventPublisher restaurantEventPublisher;
    @InjectMocks private RestaurantService restaurantService;

    @Test
    void shouldCreateRestaurantSuccessfully() {
        RestaurantRequestDTO dto = new RestaurantRequestDTO(
                "Restaurant Name",
                "restaurant@gmail.com",
                "Some Address",
                "",
                new Address()
        );

        Restaurant restaurantEntity = new Restaurant();
        when(mapper.toEntity(dto)).thenReturn(restaurantEntity);
        when(repository.save(restaurantEntity)).thenReturn(restaurantEntity);

        Restaurant result = restaurantService.createRestaurant(dto);

        assertNotNull(result);
        assertEquals(RestaurantStatus.OPEN, result.getStatus());

        verify(validator, times(1)).checkIfExistRestaurantWithSameEmail(dto.email());
        verify(mapper, times(1)).toEntity(dto);
        verify(repository, times(1)).save(restaurantEntity);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RestaurantRequestDTO dto = new RestaurantRequestDTO(
                "Restaurant Name",
                "restaurant@gmail.com",
                "Some Address",
                "",
                new Address()
        );

        doThrow(new RestaurantFoundException("This email already exit"))
                .when(validator).checkIfExistRestaurantWithSameEmail(dto.email());

        RestaurantFoundException ex = assertThrows(
                RestaurantFoundException.class,
                () -> restaurantService.createRestaurant(dto)
        );

        assertEquals("This email already exit", ex.getMessage());
        verify(validator, times(1)).checkIfExistRestaurantWithSameEmail(dto.email());
        verify(mapper, never()).toEntity(dto);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldFindRestaurantByIdSuccessfully(){
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setAuditStatus(AuditStatus.ACTIVE);

        when(repository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        Restaurant result = assertDoesNotThrow(() -> restaurantService.findRestaurantById(restaurantId));

        assertNotNull(result);
        assertEquals(AuditStatus.ACTIVE, restaurant.getAuditStatus());
        assertEquals(restaurantId, restaurant.getId());

        verify(repository, times(1)).findById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenFindRestaurantById(){
        UUID restaurantId = UUID.randomUUID();

        when(repository.findById(restaurantId)).thenReturn(Optional.empty());

        RestaurantNotFoundException ex = assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.findRestaurantById(restaurantId)
        );
        assertEquals(String.format("Restaurant ID: %s not found", restaurantId), ex.getMessage());

        verify(repository, times(1)).findById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIsDeleted(){
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setAuditStatus(AuditStatus.DELETED);

        RestaurantNotFoundException ex = assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.findRestaurantById(restaurantId)
        );
        assertEquals(String.format("Restaurant ID: %s not found", restaurantId), ex.getMessage());

        verify(repository, times(1)).findById(restaurantId);
    }

    @Test
    void shouldToggleRestaurantStatusToActiveSuccessfully(){
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setAuditStatus(AuditStatus.ACTIVE);
        restaurant.setStatus(RestaurantStatus.OPEN);

        when(repository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(repository.save(restaurant)).thenReturn(restaurant);

        assertDoesNotThrow(() -> restaurantService.toggleRestaurantStatus(restaurantId));

        assertEquals(RestaurantStatus.CLOSED, restaurant.getStatus());

        verify(repository, times(1)).findById(restaurantId);
        verify(repository, times(1)).save(restaurant);
        verify(redisService, times(1)).insertRestaurantInCache(restaurant);
    }

    @Test
    void shouldToggleRestaurantStatusToClosedSuccessfully(){
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setAuditStatus(AuditStatus.ACTIVE);
        restaurant.setStatus(RestaurantStatus.OPEN);

        when(repository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(repository.save(restaurant)).thenReturn(restaurant);

        assertDoesNotThrow(() -> restaurantService.toggleRestaurantStatus(restaurantId));

        assertEquals(RestaurantStatus.CLOSED, restaurant.getStatus());

        verify(repository, times(1)).findById(restaurantId);
        verify(repository, times(1)).save(restaurant);
        verify(redisService, times(1)).insertRestaurantInCache(restaurant);
    }

    @Test
    void shouldThrowExceptionWhenToggleRestaurantAndIsNotFound(){
        UUID restaurantId = UUID.randomUUID();

        when(repository.findById(restaurantId)).thenReturn(Optional.empty());

        RestaurantNotFoundException ex = assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.findRestaurantById(restaurantId)
        );
        assertEquals(String.format("Restaurant ID: %s not found", restaurantId), ex.getMessage());

        verify(repository, times(1)).findById(restaurantId);
        verify(repository, never()).save(any(Restaurant.class));
        verify(redisService, never()).insertRestaurantInCache(any(Restaurant.class));
    }

    @Test
    void shouldDisableRestaurantByIdSuccessfully(){
        UUID restaurantId = UUID.randomUUID();

        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .menus(List.of(Menu.builder().auditStatus(AuditStatus.ACTIVE).build()))
                .status(RestaurantStatus.OPEN)
                .auditStatus(AuditStatus.ACTIVE)
                .build();

        when(repository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(repository.save(restaurant)).thenReturn(restaurant);
        doNothing().when(restaurantEventPublisher).publisherInRestaurantDeleted(any(RestaurantDeletedEvent.class));

        assertDoesNotThrow(() -> restaurantService.disableRestaurantById(restaurantId));

        assertEquals(AuditStatus.DELETED, restaurant.getAuditStatus());

        verify(repository, times(1)).save(restaurant);
        verify(repository, times(1)).findById(restaurantId);
        verify(restaurantEventPublisher, times(1)).publisherInRestaurantDeleted(any(RestaurantDeletedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenTryDisableRestaurantById(){
        UUID restaurantId = UUID.randomUUID();

        when(repository.findById(restaurantId)).thenReturn(Optional.empty());

        RestaurantNotFoundException ex = assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.disableRestaurantById(restaurantId)
        );
        assertEquals(String.format("Restaurant ID: %s not found", restaurantId), ex.getMessage());

        verify(repository, times(1)).findById(restaurantId);
        verify(repository, never()).save(any(Restaurant.class));
        verify(restaurantEventPublisher, never()).publisherInRestaurantDeleted(any(RestaurantDeletedEvent.class));
    }
}
