package com.systemdelivery.authentication.dto;

import com.systemdelivery.authentication.controller.dto.LoginRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Stream<Arguments> generateLoginArguments() {
        return Stream.of(
                Arguments.of("", "jonh12345", "Email is required"),
                Arguments.of("jonh@gmail.com", "", "Password is required"),
                Arguments.of(null, "jonh12345", "Email is required"),
                Arguments.of("jonh@gmail.com", null, "Password is required")
        );
    }

    @ParameterizedTest(name = "Invalid field test #{index} - expecting: {2}")
    @MethodSource("generateLoginArguments")
    public void shouldRefuseLoginWithInvalidFields(String email, String password, String expectedMessage) {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(expectedMessage)),
                "Expected to find message: " + expectedMessage);
    }

    @Test
    void shouldLoggerUserSuccessfully() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("jonh@gmai.com", "jonh123456");

        assertDoesNotThrow(() -> validator.validate(loginRequestDTO));
    }
}
