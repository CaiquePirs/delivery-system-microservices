package com.systemdelivery.authentication.client.service;

import com.systemdelivery.authentication.client.api.ApiCustomerClient;
import com.systemdelivery.authentication.client.api.ApiRestaurantClient;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.CreateCustomerRequestDTO;
import com.systemdelivery.authentication.controller.dto.CreateRestaurantRequestDTO;
import com.systemdelivery.authentication.controller.dto.CustomerResponseDTO;
import com.systemdelivery.authentication.controller.dto.RestaurantResponseDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiClientService {

    private final ApiCustomerClient apiCustomerClient;
    private final ApiRestaurantClient apiRestaurantClient;

    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO customerRequestDTO) {
        try {
            ResponseEntity<CustomerResponseDTO> responseEntity = apiCustomerClient.createCustomer(customerRequestDTO);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();

            } else if (responseEntity.getStatusCode().equals(HttpStatus.CONFLICT)) {
                throw new ErrorRegisterException("This email already exists: " +  customerRequestDTO.email());

            } else {
                throw new ErrorRegisterException("Error when registering customer");
            }

        } catch (FeignException e) {
            throw new ErrorRegisterException("Error when create customer: " + e.getMessage());
        }
    }

    public RestaurantResponseDTO createRestaurant(CreateRestaurantRequestDTO restaurantRequestDTO) {
        try {
            ResponseEntity<RestaurantResponseDTO> responseEntity = apiRestaurantClient.createRestaurant(restaurantRequestDTO);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();

            } else if (responseEntity.getStatusCode().equals(HttpStatus.CONFLICT)) {
                throw new ErrorRegisterException("This email already exists: " + restaurantRequestDTO.email());

            } else {
                throw new ErrorRegisterException("Error when registering restaurant");
            }
        } catch (FeignException e) {
            throw new ErrorRegisterException("Error when registering restaurant: " + e.getMessage());
        }
    }

    public void deleteRestaurantById(UUID restaurantId) {
        try {
            apiRestaurantClient.deleteRestaurant(restaurantId);

        } catch (FeignException e) {
            throw new ErrorRegisterException("Error when deleting restaurant: " + e.getMessage());
        }
    }

    public void deleteCustomerById(UUID customerId) {
        try {
            apiCustomerClient.deleteCustomer(customerId);

        } catch (FeignException e) {
            throw new ErrorRegisterException("Error when deleting customer: " + e.getMessage());
        }
    }


}
