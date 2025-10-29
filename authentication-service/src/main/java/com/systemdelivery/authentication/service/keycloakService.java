package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class keycloakService {

    @Value("${spring.keycloak.client-id}")
    private String CLIENT_ID;

    @Value("${spring.keycloak.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.keycloak.token-url}")
    private String TOKEN_URL;

    public LoginResponseDTO findUserInKeycloak(LoginRequestDTO loginRequest){
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "password");
            params.add("client_id", CLIENT_ID);
            params.add("client_secret", CLIENT_SECRET);
            params.add("username", loginRequest.email());
            params.add("password", loginRequest.password());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(
                    TOKEN_URL,
                    new HttpEntity<>(params, headers),
                    LoginResponseDTO.class
            );

            return loginResponse.getBody();

        } catch (Exception e){
            log.error("Error when trying to authenticate user in Keycloak: {}", e.getMessage());
            throw new ErrorLoginException("Email or Password Invalid.");
        }
    }
}
