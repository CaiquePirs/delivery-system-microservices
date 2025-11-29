package com.deliverysistem.customers.service;

import com.customers.controller.advice.exceptions.NotFoundException;
import com.customers.controller.dto.AddressRequestDTO;
import com.customers.mapper.AddressMapper;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.repository.AddressRepository;
import com.customers.service.AddressService;
import com.deliverysistem.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AddressesServiceTest {

    @Mock private AddressRepository addressRepository;
    @InjectMocks private AddressService addressService;

    private AddressMapper addressMapper;

    @BeforeEach
    void setUp(){
        addressMapper = new AddressMapper();
    }

    @Test
    void shouldCreateAddressSuccessfully() {
        AddressRequestDTO dto = TestUtils.addressRequestDTO();
        Customer customer = TestUtils.customer();

        Address mappedAddress = Address.builder()
                .city(dto.city())
                .state(dto.state())
                .neighborhood(dto.neighborhood())
                .country(dto.country())
                .number(dto.number())
                .zipcode(dto.zipcode())
                .street(dto.street())
                .customer(customer)
                .build();

        Address persistedAddress = Address.builder()
                .id(UUID.randomUUID())
                .city(dto.city())
                .state(dto.state())
                .neighborhood(dto.neighborhood())
                .country(dto.country())
                .number(dto.number())
                .zipcode(dto.zipcode())
                .street(dto.street())
                .customer(customer)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(addressMapper.mapToEntity(dto)).thenReturn(mappedAddress);
        when(addressRepository.save(mappedAddress)).thenReturn(persistedAddress);

        Address result = assertDoesNotThrow(() -> addressService.createAddress(dto, customer));

        assertNotNull(result.getId());
        assertAll(
                () -> assertEquals(dto.city(), result.getCity()),
                () -> assertEquals(dto.state(), result.getState()),
                () -> assertEquals(dto.country(), result.getCountry()),
                () -> assertEquals(dto.zipcode(), result.getZipcode()),
                () -> assertEquals(dto.neighborhood(), result.getNeighborhood()),
                () -> assertEquals(dto.street(), result.getStreet()),
                () -> assertEquals(customer, result.getCustomer())
        );

        verify(addressMapper, times(1)).mapToEntity(dto);
        verify(addressRepository, times(1)).save(mappedAddress);
    }

    @Test
    void shouldFindAddressByIdSuccessfully(){
        UUID addressId = UUID.randomUUID();
        Address address = TestUtils.address();
        address.setId(addressId);
        address.setCustomer(new Customer());

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        Address result = assertDoesNotThrow(() -> addressService.findById(addressId));

        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    void shouldThrowExceptionWhenAddressNotFound(){
        UUID addressId = UUID.randomUUID();

        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        NotFoundException ex =  assertThrows(
                NotFoundException.class,
                () -> addressService.findById(addressId)
        );
        assertEquals("Address ID not found", ex.getMessage());

        verify(addressRepository, times(1)).findById(addressId);
    }

}
