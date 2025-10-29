package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final keycloakService keycloakService;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponse = keycloakService.findUserInKeycloak(loginRequest);

        if (loginResponse == null) {
            throw new ErrorLoginException("Email or Password Invalid.");
        }

        return loginResponse;
    }

}
