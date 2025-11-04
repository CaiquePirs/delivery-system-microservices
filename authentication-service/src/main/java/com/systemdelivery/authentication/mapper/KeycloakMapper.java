package com.systemdelivery.authentication.mapper;

import com.systemdelivery.authentication.controller.dto.CreateCustomerRequestDTO;
import com.systemdelivery.authentication.controller.dto.CreateRestaurantRequestDTO;
import com.systemdelivery.authentication.controller.dto.UserKeycloakDTO;
import com.systemdelivery.authentication.model.UserRoleType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeycloakMapper {

    public UserKeycloakDTO mapToKeycloakUserByCustomer(CreateCustomerRequestDTO dto, UUID userId) {
        return UserKeycloakDTO.builder()
                .firstName(dto.name())
                .lastName(dto.name())
                .password(dto.password())
                .email(dto.email())
                .userCreatedId(userId)
                .role(UserRoleType.CUSTOMER)
                .build();
    }

    public UserKeycloakDTO mapToKeycloakUserByRestaurant(CreateRestaurantRequestDTO dto,  UUID userId) {
        return UserKeycloakDTO.builder()
                .firstName(dto.name())
                .lastName(dto.name())
                .password(dto.password())
                .email(dto.email())
                .userCreatedId(userId)
                .role(UserRoleType.RESTAURANT)
                .build();
    }
}
