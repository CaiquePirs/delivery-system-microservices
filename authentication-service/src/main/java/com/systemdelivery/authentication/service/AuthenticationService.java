package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.client.service.ApiClientService;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.*;
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
    private final ApiClientService apiClientService;
    private final KeycloakMapper keycloakMapper;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO tokenInCache = redisService.findUserTokenInCache(loginRequest.email());
            if (tokenInCache != null) {
                return tokenInCache;
            }

            LoginResponseDTO loginResponse = keycloakService.loginInKeycloak(loginRequest);
            redisService.insertUserTokenInCache(loginRequest.email(), loginResponse);

            return loginResponse;
        } catch (Exception e) {
            throw new ErrorLoginException("Email or Password invalid");
        }
    }

    public CustomerResponseDTO signUpCustomer(CreateCustomerRequestDTO customerRequest) {
        CustomerResponseDTO customerResponse = apiClientService.createCustomer(customerRequest);
        if(customerResponse == null) {
            throw new ErrorRegisterException("Error when creating the customer");
        }

        try {
            UserKeycloakDTO userKeycloak = keycloakMapper.mapToKeycloakUserByCustomer(customerRequest, customerResponse.id());
            keycloakService.registerUserInKeycloak(userKeycloak);
            return customerResponse;

        } catch (Exception e){
            apiClientService.deleteCustomerById(customerResponse.id());
            throw new ErrorRegisterException("Error when creating the customer " + e.getMessage());
        }
    }

    public RestaurantResponseDTO signUpRestaurant(CreateRestaurantRequestDTO restaurantRequest) {
        RestaurantResponseDTO restaurantResponse = apiClientService.createRestaurant(restaurantRequest);
        if(restaurantResponse == null) {
            throw new ErrorRegisterException("Error creating restaurant");
        }

        try {
            UserKeycloakDTO userKeycloak = keycloakMapper.mapToKeycloakUserByRestaurant(restaurantRequest, restaurantResponse.id());
            keycloakService.registerUserInKeycloak(userKeycloak);
            return restaurantResponse;

        } catch (Exception e){
            apiClientService.deleteRestaurantById(restaurantResponse.id());
            throw new ErrorRegisterException("Error registering the user with error: " + e.getMessage());
        }
    }

    public void disableUserByEmail(String email){
        keycloakService.disableUserByEmail(email);

        log.info("User deleted successfully: {}", email);

        redisService.removerUserTokenFromCache(email);
    }
}
