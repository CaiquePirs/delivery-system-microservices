package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.dto.InternalLoginDTO;
import com.systemdelivery.authentication.validator.AuthenticationValidator;
import com.systemdelivery.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationValidatorTest {

    private AuthenticationValidator authenticationValidator;

    @BeforeEach
    void setUp(){
        authenticationValidator = new AuthenticationValidator();
    }

    @Test
    void shouldValidateInternalServiceLoginSuccessfully(){
        InternalLoginDTO internalLoginDTO = TestUtils.internalLoginDTO();

        assertDoesNotThrow(() -> authenticationValidator.validateInternalServiceLogin(internalLoginDTO));
        assertNotNull(internalLoginDTO.clientId());
        assertNotNull(internalLoginDTO.clientSecret());
    }

    @Test
    void shouldThrowExceptionWhenClientIdIsNull(){
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO(null, "cswj392-d2c09cc0d92jdd");

        ErrorLoginException ex = assertThrows(
                ErrorLoginException.class,
                () -> authenticationValidator.validateInternalServiceLogin(internalLoginDTO)
        );

        assertEquals("Client Id cannot be empty", ex.getMessage());
        assertNull(internalLoginDTO.clientId());
    }

    @Test
    void shouldThrowExceptionWhenClientIdIsBlank(){
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO(null, "");

        ErrorLoginException ex = assertThrows(
                ErrorLoginException.class,
                () -> authenticationValidator.validateInternalServiceLogin(internalLoginDTO)
        );

        assertEquals("Client Id cannot be empty", ex.getMessage());
        assertNull(internalLoginDTO.clientId());
        assertTrue(internalLoginDTO.clientSecret().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenClientSecretIsNull(){
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO("CLIENT_ID_TEST",  null);

        ErrorLoginException ex = assertThrows(
                ErrorLoginException.class,
                () -> authenticationValidator.validateInternalServiceLogin(internalLoginDTO)
        );

        assertEquals("Client Secret cannot be empty", ex.getMessage());
        assertNull(internalLoginDTO.clientSecret());
    }

    @Test
    void shouldThrowExceptionWhenClientSecretIsBlank(){
        InternalLoginDTO internalLoginDTO = new InternalLoginDTO("cswj392-d2c09cc0d92jdd",  "");

        ErrorLoginException ex = assertThrows(
                ErrorLoginException.class,
                () -> authenticationValidator.validateInternalServiceLogin(internalLoginDTO)
        );

        assertTrue(internalLoginDTO.clientSecret().isEmpty());
        assertEquals("Client Secret cannot be empty", ex.getMessage());
    }
}
