package com.deliverysystem.delivery.mapper;

import com.deliverysystem.delivery.controller.dtos.CurrierRequestDTO;
import com.deliverysystem.delivery.controller.dtos.CurrierResponseDTO;
import com.deliverysystem.delivery.model.Currier;
import org.springframework.stereotype.Component;

@Component
public class CurrierMapper {

    public Currier mapToEntity(CurrierRequestDTO currierDTO){
        return Currier.builder()
                .name(currierDTO.name())
                .email(currierDTO.email())
                .phone(currierDTO.phone())
                .vehicleType(currierDTO.vehicleType())
                .build();
    }

    public CurrierResponseDTO mapToResponse(Currier currier){
        return CurrierResponseDTO.builder()
                .id(currier.getId())
                .name(currier.getName())
                .email(currier.getEmail())
                .vehicleType(currier.getVehicleType())
                .phone(currier.getPhone())
                .build();
    }
}
