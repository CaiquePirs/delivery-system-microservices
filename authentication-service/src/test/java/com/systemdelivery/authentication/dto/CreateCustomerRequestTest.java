package com.systemdelivery.authentication.dto;

import com.systemdelivery.authentication.controller.dto.CreateAddressRequestDTO;
import com.systemdelivery.authentication.controller.dto.CreateCustomerRequestDTO;
import com.systemdelivery.utils.TestUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateCustomerRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Stream<Arguments> provideInvalidArgumentRequest() {
        return Stream.of(
                Arguments.of("Jonh", "jonh.com", "jonh123456", "(10) 4444444", TestUtils.createAddressRequestDTO(), "Email must be valid"),
                Arguments.of(null, "jonh@gmail.com", "jonh123456", "(10) 4444444", TestUtils.createAddressRequestDTO(), "Name is required"),
                Arguments.of("Jonh", null, "jonh123456", "(10) 4444444", TestUtils.createAddressRequestDTO(), "Email is required"),
                Arguments.of("Jonh", "jonh@gmail.com", null, "(10) 4444444", TestUtils.createAddressRequestDTO(), "Password is required"),
                Arguments.of("Jonh", "jonh@gmail.com", "jonh123456", null, TestUtils.createAddressRequestDTO(), "Phone is required"),
                Arguments.of("Jonh", "jonh@gmail.com", "jonh123456", "(10) 4444444", null, "Address is required")
        );
    }

    @ParameterizedTest(name = "Invalid field test #{index} - expecting: {6}")
    @MethodSource("provideInvalidArgumentRequest")
    void shouldRefuseInvalidFieldTest(String name, String email, String phone, String password, CreateAddressRequestDTO address, String expectedMessage) {
        CreateCustomerRequestDTO createCustomerRequestDTO = new CreateCustomerRequestDTO(name, email, phone, password, address);
        Set<ConstraintViolation<CreateCustomerRequestDTO>> violations = validator.validate(createCustomerRequestDTO);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(expectedMessage)));
    }

    @Test
    void mustAcceptRequestWithValidFields() {
        CreateCustomerRequestDTO createCustomerRequestDTO = TestUtils.createCustomerRequestDTO();

        assertDoesNotThrow(() -> validator.validate(createCustomerRequestDTO));
    }

}
