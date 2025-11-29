package com.deliverysistem.customers.service;

import com.customers.controller.advice.exceptions.CustomerFoundException;
import com.customers.controller.advice.exceptions.CustomerNotAuthorizedException;
import com.customers.controller.advice.exceptions.NotFoundException;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.model.enums.AuditStatus;
import com.customers.repository.CustomerRepository;
import com.customers.validator.CustomerValidator;
import com.deliverysistem.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerValidatorTest {

    @Mock private CustomerRepository customerRepository;
    @InjectMocks private CustomerValidator customerValidator;

    private Jwt jwt;
    private Customer customer;
    private UUID customerId;
    private UUID addressId;
    private Address address;

    @BeforeEach
    void setUp(){
        customerId = UUID.randomUUID();
        customer = TestUtils.customer();
        customer.setId(customerId);
        customer.setStatus(AuditStatus.ACTIVE);

        addressId = UUID.randomUUID();
        address = TestUtils.address();
        address.setId(addressId);
        address.setCustomer(customer);

        customer.getAddresses().add(address);

        jwt = new Jwt(
                "fccsjsfj209fjjsjwiw928394....",
                Instant.now(),
                Instant.now().plus(Duration.ofDays(365)),
                Map.of("Authorization", new Object()),
                Map.of("customer_id", customerId.toString())
        );
    }

    @Test
    void mustPassTestWhenNoCustomerWithSameEmailExists(){
        String email = "test@gmail.com";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> customerValidator.checkIfExistCustomerWithSameEmail(email));

        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenExistCustomerWithSameEmail(){
        String email = "test@gmail.com";
        Customer customer = TestUtils.customer();
        customer.setEmail(email);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        CustomerFoundException ex = assertThrows(
                CustomerFoundException.class,
                () -> customerValidator.checkIfExistCustomerWithSameEmail(email)
        );

        assertEquals("This email already exist", ex.getMessage());
        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should validate authenticated customer owner when is customer owner")
    void shouldValidateAuthenticatedCustomerOwnershipSuccessfully(){
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerValidator.validateAuthenticatedCustomerOwnership(jwt, addressId));

        assertEquals(customer.getId().toString(), jwt.getClaim("customer_id"));
        assertTrue(customer.getAddresses().contains(address));

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdLoggedIsNull(){
        Jwt jwtAuth = new Jwt(
                "fccsjsfj209fjjsjwiw928394....",
                Instant.now(),
                Instant.now().plus(Duration.ofDays(365)),
                Map.of("Authorization", new Object()),
                Map.of("", "")
        );

        CustomerNotAuthorizedException ex = assertThrows(
                CustomerNotAuthorizedException.class,
                () -> customerValidator.validateAuthenticatedCustomerOwnership(jwtAuth, addressId)
        );

        assertEquals("Customer not authorized to perform this request", ex.getMessage());
        assertTrue(Optional.ofNullable(jwtAuth.getClaim("customer_id")).isEmpty());

        verify(customerRepository, never()).findById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenIsNotCustomerOwner(){
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        customer.getAddresses().clear();

        CustomerNotAuthorizedException ex = assertThrows(
                CustomerNotAuthorizedException.class,
                () -> customerValidator.validateAuthenticatedCustomerOwnership(jwt, addressId)
        );
        assertEquals("Customer not authorized to perform this request", ex.getMessage());
        assertTrue(customer.getAddresses().isEmpty());
        assertFalse(customer.getAddresses().contains(address));

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldResolverAndFindCustomerLoggedSuccessfully(){
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerValidator.resolverAndFindCustomerLogged(jwt));
        assertFalse(jwt.getClaimAsString("customer_id").isEmpty());

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldTrowExceptionWhenCustomerIdLoggedIsNull(){
        Jwt jwtAuth = new Jwt(
                "fccsjsfj209fjjsjwiw928394....",
                Instant.now(),
                Instant.now().plus(Duration.ofDays(365)),
                Map.of("Authorization", new Object()),
                Map.of("", "")
        );

        CustomerNotAuthorizedException ex = assertThrows(
                CustomerNotAuthorizedException.class,
                () -> customerValidator.resolverAndFindCustomerLogged(jwtAuth)
        );
        assertEquals("Customer not authorized to perform this request ", ex.getMessage());

        verify(customerRepository, never()).findById(customerId);
    }

    @Test
    @DisplayName("It should throw an exception when it tries to find a logged-in customer but they don't exist.")
    void shouldThrowExceptionWhenCustomerLoggedNotExist(){
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerValidator.resolverAndFindCustomerLogged(jwt)
        );

        assertEquals("Customer ID not found", ex.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldFindCustomerByIdSuccessfully(){
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer result = assertDoesNotThrow(() -> customerValidator.findCustomerById(customerId));

        assertNotNull(result);
        assertEquals(customerId, result.getId());

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIsNotFound(){
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerValidator.findCustomerById(customerId)
        );

        assertEquals("Customer ID not found", ex.getMessage());

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void AnExceptionShouldThrownWhenCustomerIsFoundButStatusIsDeleted(){
        customer.setStatus(AuditStatus.DELETED);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerValidator.findCustomerById(customerId)
        );

        assertEquals("Customer ID not found", ex.getMessage());
        assertEquals(AuditStatus.DELETED, customer.getStatus());

        verify(customerRepository, times(1)).findById(customerId);
    }

}
