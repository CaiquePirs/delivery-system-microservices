package com.customers.service;

import com.customers.controller.dto.AddressRequestDTO;
import com.customers.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    public List<Address> mapAddressToEntity(List<AddressRequestDTO> requests){
        return requests.stream().map(dto -> Address.builder()
                        .street(dto.street())
                        .zipcode(dto.zipcode())
                        .city(dto.city())
                        .number(dto.number())
                        .neighborhood(dto.neighborhood())
                        .state(dto.state())
                        .country(dto.country())
                        .build()
        ).toList();
    }


}
