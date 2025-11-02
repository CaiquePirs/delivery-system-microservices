package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.CreateUserRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import com.systemdelivery.authentication.controller.dto.UserKeycloakDTO;
import com.systemdelivery.authentication.event.publisher.UserEventPublisher;
import com.systemdelivery.authentication.event.representation.CustomerEventResponse;
import com.systemdelivery.authentication.event.representation.enums.RegisterEventStatus;
import com.systemdelivery.authentication.mapper.KeycloakMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final keycloakService keycloakService;
    private final RedisService redisService;
    private final UserEventPublisher userEventPublisher;
    private final KeycloakMapper keycloakMapper;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO tokenInCache = redisService.findUserTokenInCache(loginRequest.email());
            if(tokenInCache != null) {
                return tokenInCache;
            }

            LoginResponseDTO loginResponse = keycloakService.loginInKeycloak(loginRequest);
            redisService.insertUserTokenInCache(loginRequest.email(), loginResponse);

            return loginResponse;
        } catch (Exception e){
            throw new ErrorLoginException("Error when logging in with the error: " + e.getMessage());
        }
    }

    public CustomerEventResponse signUpUser(CreateUserRequestDTO customerRequest) {
        try {
            CustomerEventResponse customerResponse = userEventPublisher.publishInCreateNewCustomer(customerRequest);

            if(customerResponse == null || customerResponse.status().equals(RegisterEventStatus.ERROR)) {
                throw new ErrorRegisterException("This email already exists: " +  customerRequest.email());
            }

            UserKeycloakDTO userKeycloak = keycloakMapper.mapToKeycloakUser(customerRequest);
            keycloakService.registerUserInKeycloak(userKeycloak);

            return customerResponse;
        } catch (Exception e){
            throw new ErrorRegisterException("Error registering the user with the error: " + e.getMessage());
        }
    }
}
