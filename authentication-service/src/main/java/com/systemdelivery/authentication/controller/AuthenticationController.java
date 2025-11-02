package com.systemdelivery.authentication.controller;

import com.systemdelivery.authentication.controller.dto.CreateUserRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import com.systemdelivery.authentication.event.representation.CustomerEventResponse;
import com.systemdelivery.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/signUp")
    public ResponseEntity<CustomerEventResponse> registerCustomer(
            @RequestBody @Valid CreateUserRequestDTO customerRequest){

        CustomerEventResponse customerResponse = authenticationService.signUpUser(customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

}
