package com.systemdelivery.authentication.dto;

import com.systemdelivery.authentication.controller.dto.CreateAddressRequestDTO;
import com.systemdelivery.authentication.controller.dto.CreateRestaurantRequestDTO;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateRestaurantRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public Stream<Arguments> provideInvalidArgumentRequest(){
        return Stream.of(
                Arguments.of(null, "pizzaplace@gmail.com", "pizza12345", "pizzaplace.com", "delicious pizzas",
                        TestUtils.createAddressRequestDTO(), "Name is required"),

                Arguments.of("Pizza Place", null, "pizza12345", "pizzaplace.com", "delicious pizzas",
                        TestUtils.createAddressRequestDTO(), "Email is required"),

                Arguments.of("Pizza Place", "pizzaplace.com", "pizza12345", "pizzaplace.com", "delicious pizzas",
                        TestUtils.createAddressRequestDTO(), "Email must be valid"),

                Arguments.of("Pizza Place", "pizzaplace@gmail.com", null, "pizzaplace.com", "delicious pizzas",
                        TestUtils.createAddressRequestDTO(), "Password is required"),

                Arguments.of("Pizza Place", "pizzaplace@gmail.com", "pizza12345", null, "delicious pizzas",
                        TestUtils.createAddressRequestDTO(), "Website is required"),

                Arguments.of("Pizza Place", "pizzaplace@gmail.com", "pizza12345", "pizzaplace.com", null,
                        TestUtils.createAddressRequestDTO(), "Description is required"),

                Arguments.of("Pizza Place", "pizzaplace@gmail.com", "pizza12345", "pizzaplace.com", "delicious pizzas",
                        null, "Address is required")
        );
    }

    @ParameterizedTest(name = "Invalid field test #{index} - expecting: {6}")
    @MethodSource("provideInvalidArgumentRequest")
    void shouldRefuseInvalidFieldTest(String name, String email, String password, String website,
                                      String description, CreateAddressRequestDTO address,  String expectedMessage){

        CreateRestaurantRequestDTO createRestaurantRequestDTO = new CreateRestaurantRequestDTO(
                name, email, password, website, description, address);

        Set<ConstraintViolation<CreateRestaurantRequestDTO>> violations = validator.validate(createRestaurantRequestDTO);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(expectedMessage)));
    }


    @Test
    void mustAcceptRequestWithValidFields() {
        CreateRestaurantRequestDTO createRestaurantRequestDTO = TestUtils.createRestaurantRequestDTO();

        assertDoesNotThrow(() -> validator.validate(createRestaurantRequestDTO));
    }


}
