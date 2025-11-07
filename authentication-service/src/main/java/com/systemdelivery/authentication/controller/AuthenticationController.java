package com.systemdelivery.authentication.controller;

import com.systemdelivery.authentication.controller.dto.*;
import com.systemdelivery.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO login){
        LoginResponseDTO response = authenticationService.login(login);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/internal-login")
    public ResponseEntity<LoginResponseDTO> getInternalAccessToken(@RequestBody InternalLoginDTO internalLoginDTO){
        LoginResponseDTO response = authenticationService.authenticateInternalClient(internalLoginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signUp-customers")
    public ResponseEntity<CustomerResponseDTO> registerCustomer(@RequestBody @Valid CreateCustomerRequestDTO dto){
        CustomerResponseDTO customerResponse = authenticationService.signUpCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

    @PostMapping("/signUp-restaurants")
    public ResponseEntity<RestaurantResponseDTO> registerRestaurant(@RequestBody @Valid CreateRestaurantRequestDTO dto){
        RestaurantResponseDTO restaurantResponse = authenticationService.signUpRestaurant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantResponse);
    }
}
