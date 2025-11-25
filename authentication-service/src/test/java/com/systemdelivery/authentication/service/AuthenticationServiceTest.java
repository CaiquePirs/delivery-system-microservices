package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.client.service.ApiClientService;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.*;
import com.systemdelivery.authentication.mapper.KeycloakMapper;
import com.systemdelivery.authentication.model.UserRoleType;
import com.systemdelivery.authentication.validator.AuthenticationValidator;
import com.systemdelivery.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks private AuthenticationService authenticationService;
    @Mock private keycloakService keycloakService;
    @Mock private AuthenticationValidator authenticationValidator;
    @Mock private RedisService redisService;
    @Mock private ApiClientService apiClientService;
    @Mock private KeycloakMapper keycloakMapper;

    @Test
    void shouldLoginUserInKeycloakSuccessfully() {
        LoginRequestDTO loginRequestDTO = TestUtils.loginRequestDTO();
        LoginResponseDTO loginResponseDTO = TestUtils.loginResponseDTO();

        when(redisService.findUserTokenInCache(loginRequestDTO.email())).thenReturn(null);

        when(keycloakService.loginInKeycloak(loginRequestDTO)).thenReturn(loginResponseDTO);
        doNothing().when(redisService).insertUserTokenInCache(loginRequestDTO.email(), loginResponseDTO);

        LoginResponseDTO expectedLogin = assertDoesNotThrow(() -> authenticationService.login(loginRequestDTO));

        assertNotNull(expectedLogin.accessToken());
        assertNotNull(expectedLogin.tokenType());

        verify(redisService, times(1)).insertUserTokenInCache(loginRequestDTO.email(), loginResponseDTO);
        verify(keycloakService, times(1)).loginInKeycloak(loginRequestDTO);
    }

    @Test
    void shouldLoginUserBasedOnTokenInRedisCache() {
        LoginRequestDTO loginRequestDTO = TestUtils.loginRequestDTO();
        LoginResponseDTO loginResponseDTO = TestUtils.loginResponseDTO();

        when(redisService.findUserTokenInCache(loginRequestDTO.email())).thenReturn(loginResponseDTO);

        LoginResponseDTO expectedLogin = assertDoesNotThrow(() -> authenticationService.login(loginRequestDTO));

        assertNotNull(expectedLogin.tokenType());
        assertNotNull(expectedLogin.accessToken());

        verify(keycloakService, never()).loginInKeycloak(loginRequestDTO);
        verify(redisService, times(1)).findUserTokenInCache(loginRequestDTO.email());
    }

    @Test
    void shouldThrowExceptionWhenLoginFailsAndTokenIsNotFound() {
        String messageException = "Email or Password invalid";
        LoginRequestDTO loginRequestDTO = TestUtils.loginRequestDTO();

        when(redisService.findUserTokenInCache(loginRequestDTO.email())).thenReturn(null);
        doThrow(RuntimeException.class).when(keycloakService).loginInKeycloak(loginRequestDTO);

        ErrorLoginException ex = assertThrows(
                ErrorLoginException.class,
                () -> authenticationService.login(loginRequestDTO)
        );

        assertEquals(messageException, ex.getMessage());

        verify(redisService, times(1)).findUserTokenInCache(loginRequestDTO.email());
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        CreateCustomerRequestDTO customerRequestDTO = TestUtils.createCustomerRequestDTO();
        CustomerResponseDTO expectedCustomerCreated = TestUtils.customerResponseDTO();

        UserKeycloakDTO userKeycloakDTO = UserKeycloakDTO.builder()
                .firstName(customerRequestDTO.name())
                .lastName(customerRequestDTO.name())
                .email(customerRequestDTO.email())
                .role(UserRoleType.CUSTOMER)
                .password(customerRequestDTO.password())
                .userCreatedId(expectedCustomerCreated.id())
                .build();

        when(apiClientService.createCustomer(customerRequestDTO)).thenReturn(expectedCustomerCreated);
        when(keycloakMapper.mapToKeycloakUserByCustomer(customerRequestDTO, expectedCustomerCreated.id())).thenReturn(userKeycloakDTO);

        doNothing().when(keycloakService).registerUserInKeycloak(userKeycloakDTO);

        assertDoesNotThrow(() -> authenticationService.signUpCustomer(customerRequestDTO));
        assertAll(
                () -> assertEquals(customerRequestDTO.name(), expectedCustomerCreated.name()),
                () -> assertEquals(customerRequestDTO.email(), expectedCustomerCreated.email()),
                () -> assertNotNull(expectedCustomerCreated.id())
        );

        verify(apiClientService, times(1)).createCustomer(customerRequestDTO);
        verify(keycloakMapper, times(1)).mapToKeycloakUserByCustomer(customerRequestDTO, expectedCustomerCreated.id());
        verify(keycloakService, times(1)).registerUserInKeycloak(userKeycloakDTO);
    }

    @Test
    @DisplayName("Should Throw Exception When Attempting To Create Customer In Customer MicroService")
    void shouldThrowExceptionWhenTryRegisterCustomer() {
        CreateCustomerRequestDTO createCustomerRequestDTO = TestUtils.createCustomerRequestDTO();

        when(apiClientService.createCustomer(createCustomerRequestDTO)).thenThrow(ErrorRegisterException.class);

        assertThrows(ErrorRegisterException.class, () -> authenticationService.signUpCustomer(createCustomerRequestDTO));

        verify(apiClientService, times(1)).createCustomer(createCustomerRequestDTO);
        verify(keycloakService, never()).registerUserInKeycloak(any(UserKeycloakDTO.class));
    }

    @Test
    void ShouldThrowExceptionWhenAttemptingToCreateCustomerInKeycloak() {
        CreateCustomerRequestDTO createCustomerRequestDTO = TestUtils.createCustomerRequestDTO();
        CustomerResponseDTO expectedCustomerCreated = TestUtils.customerResponseDTO();

        UserKeycloakDTO userKeycloakDTO = UserKeycloakDTO.builder()
                .firstName(createCustomerRequestDTO.name())
                .lastName(createCustomerRequestDTO.name())
                .email(createCustomerRequestDTO.email())
                .role(UserRoleType.CUSTOMER)
                .password(createCustomerRequestDTO.password())
                .userCreatedId(expectedCustomerCreated.id())
                .build();

        when(apiClientService.createCustomer(createCustomerRequestDTO)).thenReturn(expectedCustomerCreated);
        doThrow(RuntimeException.class).when(keycloakService).registerUserInKeycloak(userKeycloakDTO);

        ErrorRegisterException ex = assertThrows(
                ErrorRegisterException.class,
                () -> authenticationService.signUpCustomer(createCustomerRequestDTO)
        );

        assertTrue(ex.getMessage().contains("Error when creating the customer "));

        doNothing().when(apiClientService).deleteCustomerById(expectedCustomerCreated.id());

        verify(apiClientService, times(1)).createCustomer(createCustomerRequestDTO);
        verify(apiClientService, times(1)).deleteCustomerById(expectedCustomerCreated.id());
    }

    @Test
    void shouldRegisterRestaurantSuccessfully() {
        CreateRestaurantRequestDTO restaurantRequestDTO = TestUtils.createRestaurantRequestDTO();
        RestaurantResponseDTO expectedRestaurantCreated = TestUtils.restaurantResponseDTO();

        UserKeycloakDTO userKeycloakDTO = UserKeycloakDTO.builder()
                .firstName(restaurantRequestDTO.name())
                .lastName(restaurantRequestDTO.name())
                .email(restaurantRequestDTO.email())
                .role(UserRoleType.RESTAURANT)
                .password(restaurantRequestDTO.password())
                .userCreatedId(expectedRestaurantCreated.id())
                .build();

        when(apiClientService.createRestaurant(restaurantRequestDTO)).thenReturn(expectedRestaurantCreated);
        when(keycloakMapper.mapToKeycloakUserByRestaurant(restaurantRequestDTO, expectedRestaurantCreated.id())).thenReturn(userKeycloakDTO);

        doNothing().when(keycloakService).registerUserInKeycloak(userKeycloakDTO);

        assertDoesNotThrow(() -> authenticationService.signUpRestaurant(restaurantRequestDTO));
        assertAll(
                () -> assertEquals(restaurantRequestDTO.name(), expectedRestaurantCreated.name()),
                () -> assertEquals(restaurantRequestDTO.email(), expectedRestaurantCreated.email()),
                () -> assertNotNull(expectedRestaurantCreated.id())
        );

        verify(apiClientService, times(1)).createRestaurant(restaurantRequestDTO);
        verify(keycloakMapper, times(1)).mapToKeycloakUserByRestaurant(restaurantRequestDTO, expectedRestaurantCreated.id());
        verify(keycloakService, times(1)).registerUserInKeycloak(userKeycloakDTO);
    }

    @Test
    void shouldThrowExceptionWhenTryRegisterRestaurant() {
        CreateRestaurantRequestDTO createRestaurantRequestDTO = TestUtils.createRestaurantRequestDTO();

        when(apiClientService.createRestaurant(createRestaurantRequestDTO)).thenThrow(ErrorRegisterException.class);

        assertThrows(ErrorRegisterException.class, () -> authenticationService.signUpRestaurant(createRestaurantRequestDTO));

        verify(apiClientService, times(1)).createRestaurant(createRestaurantRequestDTO);
        verify(keycloakService, never()).registerUserInKeycloak(any(UserKeycloakDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenAttemptingToCreateRestaurantInKeycloak() {
        CreateRestaurantRequestDTO createRestaurantRequestDTO = TestUtils.createRestaurantRequestDTO();
        RestaurantResponseDTO expectedRestaurantCreated = TestUtils.restaurantResponseDTO();

        UserKeycloakDTO userKeycloakDTO = UserKeycloakDTO.builder()
                .firstName(createRestaurantRequestDTO.name())
                .lastName(createRestaurantRequestDTO.name())
                .email(createRestaurantRequestDTO.email())
                .role(UserRoleType.RESTAURANT)
                .password(createRestaurantRequestDTO.password())
                .userCreatedId(expectedRestaurantCreated.id())
                .build();

        when(apiClientService.createRestaurant(createRestaurantRequestDTO)).thenReturn(expectedRestaurantCreated);
        doThrow(RuntimeException.class).when(keycloakService).registerUserInKeycloak(userKeycloakDTO);

        ErrorRegisterException ex = assertThrows(
                ErrorRegisterException.class,
                () -> authenticationService.signUpRestaurant(createRestaurantRequestDTO)
        );

        assertTrue(ex.getMessage().contains("Error registering the user with error: "));

        doNothing().when(apiClientService).deleteRestaurantById(expectedRestaurantCreated.id());

        verify(apiClientService, times(1)).createRestaurant(createRestaurantRequestDTO);
        verify(apiClientService, times(1)).deleteRestaurantById(expectedRestaurantCreated.id());
    }

    @Test
    void shouldDisableUserByEmailSuccessfully() {
        String userEmail = "user@gmail.com";

        doNothing().when(keycloakService).disableUserByEmail(userEmail);
        doNothing().when(redisService).removerUserTokenFromCache(userEmail);

        assertDoesNotThrow(() -> authenticationService.disableUserByEmail(userEmail));

        verify(keycloakService, times(1)).disableUserByEmail(userEmail);
        verify(redisService, times(1)).removerUserTokenFromCache(userEmail);
    }

    @Test
    void shouldThrowExceptionWhenTryDisableUserByEmail() {
        String userEmail = "user@gmail.com";

        doThrow(RuntimeException.class).when(keycloakService).disableUserByEmail(userEmail);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authenticationService.disableUserByEmail(userEmail)
        );

        verify(keycloakService, times(1)).disableUserByEmail(userEmail);
        verify(redisService, never()).removerUserTokenFromCache(userEmail);
    }

    @Test
    void shouldAuthenticateInternalClientByTokenInCacheSuccessfully() {
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO(
                "internal-client-id",
                "internal-client-secret"
        );

        LoginResponseDTO loginResponseDTO = TestUtils.loginResponseDTO();

        when(redisService.findUserTokenInCache(internalLoginDTO.clientId())).thenReturn(loginResponseDTO);

        LoginResponseDTO expectedLoginResponse = assertDoesNotThrow(() -> authenticationService.authenticateInternalClient(internalLoginDTO));

        assertNotNull(expectedLoginResponse.accessToken());
        assertNotNull(expectedLoginResponse.tokenType());

        verify(keycloakService, never()).getTokenAdminFromKeycloak(internalLoginDTO.clientId(), internalLoginDTO.clientSecret());
        verify(redisService, times(1)).findUserTokenInCache(internalLoginDTO.clientId());
        verify(authenticationValidator).validateInternalServiceLogin(internalLoginDTO);
    }

    @Test
    void shouldAuthenticateInternalClientWhenThereIsNoTokenInCache(){
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO(
                "internal-client-id",
                "internal-client-secret"
        );

        LoginResponseDTO loginResponseDTO = TestUtils.loginResponseDTO();

        when(redisService.findUserTokenInCache(internalLoginDTO.clientId())).thenReturn(null);
        when(keycloakService.getTokenAdminFromKeycloak(internalLoginDTO.clientId(), internalLoginDTO.clientSecret())).thenReturn(loginResponseDTO);
        doNothing().when(redisService).insertUserTokenInCache(internalLoginDTO.clientId(), loginResponseDTO);

        LoginResponseDTO expectedLogin = assertDoesNotThrow(() -> authenticationService.authenticateInternalClient(internalLoginDTO));

        assertNotNull(expectedLogin.tokenType());
        assertNotNull(expectedLogin.accessToken());

        verify(authenticationValidator, times(1)).validateInternalServiceLogin(internalLoginDTO);
        verify(redisService).insertUserTokenInCache(internalLoginDTO.clientId(), loginResponseDTO);
        verify(keycloakService, times(1)).getTokenAdminFromKeycloak(internalLoginDTO.clientId(), internalLoginDTO.clientSecret());
    }

    @Test
    void shouldThrowExceptionWhenClientIdIsNull(){
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO(
                null,
                "internal-client-secret"
        );

        doThrow(new ErrorLoginException("Client Id cannot be empty"))
                .when(authenticationValidator).validateInternalServiceLogin(internalLoginDTO);

        ErrorLoginException ex = assertThrows(
                ErrorLoginException.class,
                () -> authenticationService.authenticateInternalClient(internalLoginDTO)
        );

        assertEquals("Error retrieving token for internal service: " + "Client Id cannot be empty", ex.getMessage());

        verify(authenticationValidator, times(1)).validateInternalServiceLogin(internalLoginDTO);
        verify(redisService, never()).findUserTokenInCache(any(String.class));
        verify(keycloakService, never()).getTokenAdminFromKeycloak(any(String.class), any(String.class));
    }

    @Test
    void shouldThrowExceptionWhenClientSecretIsNull(){
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO(
                "internal-client-id",
                null
        );

        doThrow(new ErrorLoginException("Client Secret cannot be empty"))
                .when(authenticationValidator).validateInternalServiceLogin(internalLoginDTO);

        ErrorLoginException ex = assertThrows(
                ErrorLoginException.class,
                () -> authenticationService.authenticateInternalClient(internalLoginDTO)
        );

        assertEquals("Error retrieving token for internal service: " + "Client Secret cannot be empty", ex.getMessage());

        verify(authenticationValidator, times(1)).validateInternalServiceLogin(internalLoginDTO);
        verify(redisService, never()).findUserTokenInCache(any(String.class));
        verify(keycloakService, never()).getTokenAdminFromKeycloak(any(String.class), any(String.class));
    }

}