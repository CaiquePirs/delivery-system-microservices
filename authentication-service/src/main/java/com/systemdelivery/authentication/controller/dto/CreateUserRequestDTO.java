package com.systemdelivery.authentication.controller.dto;

import com.systemdelivery.authentication.event.representation.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequestDTO(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotNull(message = "User type is required")
        UserType userType,

        @NotNull(message = "Address is required")
        CreateAddressRequestDTO address) {
}
