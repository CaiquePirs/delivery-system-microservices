package com.deliverysystem.delivery.service;

import com.deliverysystem.delivery.advice.exceptions.CurrierNotFoundException;
import com.deliverysystem.delivery.controller.dtos.CurrierRequestDTO;
import com.deliverysystem.delivery.mapper.CurrierMapper;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.repositories.CurrierRepository;
import com.deliverysystem.delivery.validator.CurrierValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new CurrierNotFoundException("Currier not found with id: " + currierId));
    }
}
