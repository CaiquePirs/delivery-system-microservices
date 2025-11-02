package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.CreateCustomerRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import com.systemdelivery.authentication.event.publisher.UserEventPublisher;
import com.systemdelivery.authentication.event.representation.CustomerEventResponse;
import com.systemdelivery.authentication.event.representation.enums.RegisterEventStatus;
import com.systemdelivery.authentication.event.representation.enums.UserType;
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

    public CustomerEventResponse registerCustomer(CreateCustomerRequestDTO customerRequest) {
        CustomerEventResponse customerResponse = userEventPublisher.publishCustomerCreate(customerRequest);

        if(customerResponse == null || customerResponse.status().equals(RegisterEventStatus.ERROR)) {
            throw new ErrorRegisterException("This email already exists: " +  customerRequest.email());
        }

        keycloakService.createUser(customerRequest.email(), customerRequest.password(), UserType.CUSTOMER);
        return customerResponse;
    }
}
