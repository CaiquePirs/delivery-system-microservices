package com.systemdelivery.authentication.mapper;

import com.systemdelivery.authentication.controller.dto.CreateUserRequestDTO;
import com.systemdelivery.authentication.controller.dto.UserKeycloakDTO;
import org.springframework.stereotype.Component;

@Component
public class KeycloakMapper {

    public UserKeycloakDTO mapToKeycloakUser(CreateUserRequestDTO dto) {
        return UserKeycloakDTO.builder()
                .firstName(dto.name())
                .lastName(dto.name())
                .password(dto.password())
                .email(dto.email())
                .role(dto.userType())
                .build();
    }
}
