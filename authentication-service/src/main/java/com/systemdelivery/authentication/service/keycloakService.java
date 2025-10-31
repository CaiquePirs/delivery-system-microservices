package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import com.systemdelivery.authentication.event.representation.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.Response;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class keycloakService {

    @Value("${spring.keycloak.client-id}")  private String CLIENT_ID;
    @Value("${spring.keycloak.client-secret}") private String CLIENT_SECRET;
    @Value("${spring.keycloak.token-url}") private String TOKEN_URL;
    @Value("${KEYCLOAK_REALM}") private String REALM;
    private final Keycloak keycloakAdmin;
    private final WebClient webClient = WebClient.builder().build();

    public void createUser(String email, String password, UserType userType) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(email);
            user.setEmail(email);
            user.setEnabled(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            user.setCredentials(List.of(credential));

            UsersResource usersResource = keycloakAdmin.realm(REALM).users();
            Response response = usersResource.create(user);
            response.close();

            String userId = usersResource.search(email, true).get(0).getId();

            String roleName = switch (userType) {
                case CUSTOMER -> "ROLE_CUSTOMER";
                case RESTAURANT -> "ROLE_RESTAURANT";
            };

            RoleRepresentation role = keycloakAdmin.realm(REALM).roles().get(roleName).toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(List.of(role));

        } catch (Exception e) {
            log.error("Exception while creating user in Keycloak for email: {}", email, e);
            throw new ErrorRegisterException("Exception while creating user in Keycloak for email: " + email);
        }
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", CLIENT_ID);
            formData.add("client_secret", CLIENT_SECRET);
            formData.add("username", loginRequest.email());
            formData.add("password", loginRequest.password());

            return webClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                log.error("Login failed with status: {}", clientResponse.statusCode());
                                return Mono.error(new ErrorLoginException("Email or Password Invalid."));
                            })
                    .bodyToMono(LoginResponseDTO.class)
                    .block();

        } catch (Exception e) {
            log.error("Error when trying to authenticate user in Keycloak: {}", e.getMessage());
            throw new ErrorLoginException("Email or Password Invalid.");
        }
    }

}
