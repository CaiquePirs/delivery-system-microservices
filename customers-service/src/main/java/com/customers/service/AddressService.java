package com.customers.service;

import com.customers.controller.advice.exceptions.NotFoundException;
import com.customers.controller.dto.AddressRequestDTO;
import com.customers.mapper.AddressMapper;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final RedisService redisService;
    private final AddressMapper addressMapper;

    public Address createAddress(AddressRequestDTO dto, Customer customer){
        Address address = addressMapper.mapToEntity(dto);
        customer.getAddresses().add(address);

        redisService.insertCustomerInCache(customer);
        return addressRepository.save(address);
    }

    public Address findById(UUID addressId){
       return addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address ID not found"));
    }
}
