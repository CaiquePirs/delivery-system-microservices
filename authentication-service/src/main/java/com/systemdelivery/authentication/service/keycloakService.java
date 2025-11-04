package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import com.systemdelivery.authentication.controller.dto.UserKeycloakDTO;
import com.systemdelivery.authentication.model.UserRoleType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class keycloakService {

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @Value("${KEYCLOAK_TOKEN_URL}")
    private String TOKEN_URL;

    @Value("${KEYCLOAK_REALM}")
    private String REALM;

    private final Keycloak keycloak;
    private final RestTemplate restTemplate = new RestTemplate();

    public LoginResponseDTO loginInKeycloak(LoginRequestDTO loginRequest) throws RuntimeException {
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
    }

    public void registerUserInKeycloak(UserKeycloakDTO userKeycloakDTO) throws RuntimeException {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userKeycloakDTO.email());
        user.setEmail(userKeycloakDTO.email());
        user.setFirstName(userKeycloakDTO.firstName());
        user.setLastName(userKeycloakDTO.lastName());
        user.setEmailVerified(true);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(userKeycloakDTO.password());
        passwordCred.setTemporary(false);
        user.setCredentials(List.of((passwordCred)));

        if(userKeycloakDTO.role().equals(UserRoleType.RESTAURANT)){
            user.setAttributes(Map.of("restaurant_id", List.of(userKeycloakDTO.userCreatedId().toString())));
        } else {
            user.setAttributes(Map.of("customer_id", List.of(userKeycloakDTO.userCreatedId().toString())));
        }

        UsersResource usersResource = keycloak.realm(REALM).users();
        Response response = usersResource.create(user);

        if (response.getStatus() == HttpStatus.CREATED.value()) {
            String userId = CreatedResponseUtil.getCreatedId(response);

            RealmResource realmResource = keycloak.realm(REALM);
            RoleScopeResource rolesResource = usersResource.get(userId).roles().realmLevel();
            RoleRepresentation roleRep = realmResource.roles().get(userKeycloakDTO.role().toString()).toRepresentation();
            rolesResource.add(List.of(roleRep));
        }
    }
}
