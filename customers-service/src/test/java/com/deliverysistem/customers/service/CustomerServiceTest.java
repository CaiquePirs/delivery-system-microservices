package com.deliverysistem.customers.service;

import com.customers.controller.advice.exceptions.CustomerFoundException;
import com.customers.controller.advice.exceptions.NotFoundException;
import com.customers.controller.dto.AddressRequestDTO;
import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.event.publisher.CustomerEventPublisher;
import com.customers.event.representation.CustomerDeletedEvent;
import com.customers.mapper.AddressMapper;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.model.enums.AuditStatus;
import com.customers.repository.CustomerRepository;
import com.customers.service.CustomerService;
import com.customers.validator.CustomerValidator;
import com.deliverysistem.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock private CustomerValidator customerValidator;
    @Mock private AddressMapper addressMapper;
    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerEventPublisher customerEventPublisher;
    @InjectMocks private CustomerService customerService;

    @BeforeEach
    void setUp(){
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        CustomerRequestDTO dto = TestUtils.customerRequestDTO();
        AddressRequestDTO addressRequestDTO = dto.address();

        Address mappedAddress = TestUtils.address();
        Customer expectedSavedCustomer = TestUtils.customer();

        expectedSavedCustomer.getAddresses().add(mappedAddress);
        mappedAddress.setCustomer(expectedSavedCustomer);

        doNothing().when(customerValidator).checkIfExistCustomerWithSameEmail(dto.email());
        when(addressMapper.mapToEntity(addressRequestDTO)).thenReturn(mappedAddress);
        when(customerRepository.save(any(Customer.class))).thenReturn(expectedSavedCustomer);

        Customer result = customerService.createCustomer(dto);

        assertNotNull(result);
        assertEquals("Customer", result.getName());
        assertEquals("customer@gmail.com", result.getEmail());
        assertEquals(AuditStatus.ACTIVE, result.getStatus());

        verify(customerValidator).checkIfExistCustomerWithSameEmail(dto.email());
        verify(addressMapper).mapToEntity(addressRequestDTO);
        verify(customerRepository).save(any(Customer.class));
    }


    @Test
    @DisplayName("Should throw exception when customer email already exist with same email")
    void shouldThrowExceptionWhenCustomerAlreadyExists() {
        CustomerRequestDTO dto = TestUtils.customerRequestDTO();

        doThrow(new CustomerFoundException("This email already exist")).when(customerValidator)
                .checkIfExistCustomerWithSameEmail(dto.email());

        CustomerFoundException ex = assertThrows(
                CustomerFoundException.class,
                () -> customerService.createCustomer(dto)
        );

        assertEquals("This email already exist", ex.getMessage());

        verify(customerValidator, times(1)).checkIfExistCustomerWithSameEmail(dto.email());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldFindCustomerByIdSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = TestUtils.customer();
        customer.setId(customerId);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        Customer result = assertDoesNotThrow(() -> customerService.findCustomerById(customerId));

        assertEquals(customerId, result.getId());
        assertEquals(AuditStatus.ACTIVE, result.getStatus());
        assertNotNull(customer);

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerService.findCustomerById(customerId)
        );

        assertEquals("Customer ID not found", ex.getMessage());

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenCustomerStatusIsDeleted(){
        UUID customerId = UUID.randomUUID();
        Customer customer = TestUtils.customer();
        customer.setId(customerId);
        customer.setStatus(AuditStatus.DELETED);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerService.findCustomerById(customerId)
        );
        assertEquals(AuditStatus.DELETED, customer.getStatus());
        assertEquals("Customer ID not found", ex.getMessage());

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldDisableCustomerSuccessfully(){
        UUID customerId = UUID.randomUUID();
        Customer customer = TestUtils.customer();
        customer.setStatus(AuditStatus.ACTIVE);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerEventPublisher).publisherInCustomerDeleted(any(CustomerDeletedEvent.class));

        assertDoesNotThrow(() -> customerService.disableCustomerById(customerId));
        assertEquals(AuditStatus.DELETED, customer.getStatus());

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerEventPublisher, times(1)).publisherInCustomerDeleted(any(CustomerDeletedEvent.class));
    }

    @Test
    @DisplayName("It should throw an exception when trying to disable a client and it is not found.")
    void shouldTrowExceptionWhenTryingToDisableCustomer(){
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerService.disableCustomerById(customerId)
        );
        assertEquals("Customer ID not found", ex.getMessage());

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerEventPublisher, never()).publisherInCustomerDeleted(any(CustomerDeletedEvent.class));
    }

    @Test
    void shouldDeleteCustomerSuccessfully(){
        UUID customerId = UUID.randomUUID();
        Customer customer = TestUtils.customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerService.deleteCustomerById(customerId));

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenTryingToDeleteCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = TestUtils.customer();
        customer.setId(customerId);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerService.deleteCustomerById(customerId)
        );

        assertEquals("Customer ID not found", ex.getMessage());

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}
