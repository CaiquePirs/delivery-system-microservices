package com.deliverysystem.delivery.service;

import com.deliverysystem.delivery.controller.advice.exceptions.CurrierFoundException;
import com.deliverysystem.delivery.controller.advice.exceptions.NotFoundException;
import com.deliverysystem.delivery.controller.dtos.CurrierRequestDTO;
import com.deliverysystem.delivery.mapper.CurrierMapper;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.model.enums.VehicleType;
import com.deliverysystem.delivery.repositories.CurrierRepository;
import com.deliverysystem.delivery.validator.CurrierValidator;
import com.deliverysystem.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CurrierServiceTest {

    @Mock private CurrierRepository repository;
    @Mock private CurrierValidator validator;
    @InjectMocks private CurrierService currierService;
    @Spy private CurrierMapper currierMapper;

    @BeforeEach
    void setUp(){
        currierMapper = new CurrierMapper();
    }

    @Test
    void shouldCreateCurrierSuccessfully(){
        CurrierRequestDTO requestDTO = new CurrierRequestDTO(
                "Currier Test",
                "currier@gmail.com",
                "(99) 9999-9999",
                VehicleType.MOTORCYCLE
        );

        Currier currierSaved = Currier.builder()
                .id(UUID.randomUUID())
                .name(requestDTO.name())
                .email(requestDTO.email())
                .phone(requestDTO.phone())
                .vehicleType(requestDTO.vehicleType())
                .completedDeliveries(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        doNothing().when(validator).checkCourierEmailNotExists(requestDTO.email());
        when(currierService.create(requestDTO)).thenReturn(currierSaved);

        assertDoesNotThrow(() -> currierService.create(requestDTO));

        verify(repository, times(1)).save(any(Currier.class));
    }

    @Test
    void shouldThrowExceptionWhenCurrierAlreadyExistByEmail(){
        CurrierRequestDTO requestDTO = new CurrierRequestDTO(
                "Currier Test",
                "currier@gmail.com",
                "(99) 9999-9999",
                VehicleType.MOTORCYCLE
        );

        String messageException = "Courier with email " + requestDTO.email() + " already exists.";
        doThrow(new CurrierFoundException(messageException)).when(validator).checkCourierEmailNotExists(requestDTO.email());

        CurrierFoundException ex = assertThrows(
                CurrierFoundException.class,
                () -> currierService.create(requestDTO)
        );
        assertEquals(messageException, ex.getMessage());
        verify(repository, never()).save(any(Currier.class));
    }

    @Test
    void shouldFindCurrierByIdSuccessfully(){
        UUID currierId = UUID.randomUUID();

        Currier currier = TestUtils.currier();
        currier.setId(currierId);

        when(repository.findById(currierId)).thenReturn(Optional.of(currier));

        Currier result = assertDoesNotThrow(() -> currierService.findById(currierId));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(currier.getId(), result.getId()),
                () -> assertEquals(currier.getName(), result.getName()),
                () -> assertEquals(currier.getEmail(), result.getEmail()),
                () -> assertEquals(currier.getPhone(), result.getPhone()),
                () -> assertEquals(currier.getVehicleType(), result.getVehicleType())
        );

        verify(repository, times(1)).findById(currierId);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdIsNotFound(){
        UUID currierId = UUID.randomUUID();

        when(repository.findById(currierId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> currierService.findById(currierId)
        );

        assertEquals("Currier not found with id: " + currierId, ex.getMessage());
        verify(repository, times(1)).findById(currierId);
    }
}
