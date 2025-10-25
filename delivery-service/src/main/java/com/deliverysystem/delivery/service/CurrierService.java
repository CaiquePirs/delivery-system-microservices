package com.deliverysystem.delivery.service;

import com.deliverysystem.delivery.advice.exceptions.NotFoundException;
import com.deliverysystem.delivery.controller.dtos.CurrierRequestDTO;
import com.deliverysystem.delivery.mapper.CurrierMapper;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.model.enums.VehicleType;
import com.deliverysystem.delivery.repositories.CurrierRepository;
import com.deliverysystem.delivery.validator.CurrierValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrierService {

    private final CurrierRepository repository;
    private final CurrierValidator validator;
    private final CurrierMapper mapper;

    public Currier create(CurrierRequestDTO currierDTO) {
        validator.checkCourierEmailNotExists(currierDTO.email());
        Currier currier = mapper.mapToEntity(currierDTO);
        return repository.save(currier);
    }

    public Currier findById(UUID currierId) {
        return repository.findById(currierId)
                .orElseThrow(() -> new NotFoundException("Currier not found with id: " + currierId));
    }

    public Currier findAvailableCourierForDelivery(){
        // Simulating a delay to represent searching for an available courier
        try {
            Thread.sleep(Duration.ofMinutes(1).toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Currier currier = Currier.builder()
                .name("Currier Example")
                .email(String.format("currierExample_%s_@gmail.com", UUID.randomUUID()))
                .phone("+1234567890")
                .vehicleType(VehicleType.MOTORCYCLE)
                .completedDeliveries(new ArrayList<>())
                .build();

        return repository.save(currier);
    }

}
