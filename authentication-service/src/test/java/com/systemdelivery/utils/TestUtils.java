package com.systemdelivery.utils;

import com.systemdelivery.authentication.controller.dto.*;
import com.systemdelivery.authentication.model.UserRoleType;

import java.util.UUID;

public class TestUtils {

    public static CreateAddressRequestDTO createAddressRequestDTO() {
        return CreateAddressRequestDTO.builder()
                .city("New York")
                .country("United States")
                .neighborhood("Brooklyn")
                .number("100")
                .state("NY")
                .street("5th Avenue")
                .zipcode("10001")
                .build();
    }

    public static CreateCustomerRequestDTO createCustomerRequestDTO(){
        return CreateCustomerRequestDTO.builder()
                .name("Jonh Doe")
                .email("Jonh@gmail.com")
                .phone("1234567890")
                .password("password123")
                .address(createAddressRequestDTO())
                .build();
    }

    public static CreateRestaurantRequestDTO createRestaurantRequestDTO(){
        return CreateRestaurantRequestDTO.builder()
                .name("Pizza Place")
                .email("pizzaplace@gmail.com")
                .password("1234567890")
                .website("pizzaplace.com")
                .description("Best pizza in town")
                .address(createAddressRequestDTO())
                .build();
    }

    public static RestaurantResponseDTO restaurantResponseDTO(){
        return new RestaurantResponseDTO(
                UUID.randomUUID(),
                "Pizza Place",
                "pizzaplace@gmail.com"
        );
    }

    public static CustomerResponseDTO customerResponseDTO() {
        return new CustomerResponseDTO(
                UUID.randomUUID(),
                "Jonh Doe",
                "Jonh@gmail.com"
        );
    }

    public static RestaurantResponseDTO createRestaurant(){
        return new RestaurantResponseDTO(
                UUID.randomUUID(),
                "Pizza Place",
                "pizzaplace@gmail.com"
        );
    }

    public static LoginRequestDTO loginRequestDTO(){
        return new LoginRequestDTO(
                "Jonh@gmail.com",
                "password123"
        );
    }

    public static LoginResponseDTO loginResponseDTO(){
        return new LoginResponseDTO(
                UUID.randomUUID().toString(),
                10,
                "PASSWORD"
        );
    }

    public static InternalLoginDTO internalLoginDTO(){
        return new InternalLoginDTO(
                "CLIENT_ID_TEST",
                "cswj392-d2c09cc0d92jdd"
        );
    }

}
