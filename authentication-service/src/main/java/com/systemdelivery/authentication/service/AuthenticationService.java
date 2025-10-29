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
    private final RedisService redisService;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        LoginResponseDTO tokenInCache = redisService.findUserTokenInCache(loginRequest.email());
        if(tokenInCache != null) {
            return tokenInCache;
        }

        LoginResponseDTO loginResponse = keycloakService.findUserInKeycloak(loginRequest);
        if (loginResponse == null) {
            throw new ErrorLoginException("Email or Password Invalid.");
        }

        redisService.insertUserTokenInCache(loginRequest.email(), loginResponse);
        return loginResponse;
    }

}
