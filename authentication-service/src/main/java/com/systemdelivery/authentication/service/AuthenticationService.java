package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.client.service.ApiClientService;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.*;
import com.systemdelivery.authentication.mapper.KeycloakMapper;
import com.systemdelivery.authentication.validator.AuthenticationValidator;
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
    private final AuthenticationValidator authenticationValidator;

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
        authenticationValidator.validateIfUserIsNull(customerRequest);

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
        authenticationValidator.validateIfUserIsNull(restaurantRequest);

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
        redisService.removerUserTokenFromCache(email);
    }

    public LoginResponseDTO authenticateInternalClient(InternalLoginDTO internalLoginDTO) {
        try {
            authenticationValidator.validateInternalServiceLogin(internalLoginDTO);

            LoginResponseDTO tokenInCache = redisService.findUserTokenInCache(internalLoginDTO.clientId());
            if (tokenInCache != null) {
                return tokenInCache;
            }

            LoginResponseDTO loginResponse =  keycloakService.getTokenAdminFromKeycloak(
                    internalLoginDTO.clientId(),
                    internalLoginDTO.clientSecret()
            );

            redisService.insertUserTokenInCache(internalLoginDTO.clientId(), loginResponse);
            return loginResponse;

        } catch (Exception e){
            throw new ErrorLoginException("Error retrieving token for internal service");
        }
    }
}
