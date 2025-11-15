package com.customers.mapper;

import com.customers.controller.dto.AddressRequestDTO;
import com.customers.controller.dto.AddressResponseDTO;
import com.customers.model.Address;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AddressMapper {

    public AddressResponseDTO mapToResponse(Address address){
        return AddressResponseDTO.builder()
                .id(address.getId())
                .state(address.getState())
                .city(address.getCity())
                .zipcode(address.getZipcode())
                .neighborhood(address.getNeighborhood())
                .number(address.getNumber())
                .street(address.getStreet())
                .country(address.getCountry())
                .build();
    }

    public Address mapToEntity(AddressRequestDTO dto) {
        return Address.builder()
                .zipcode(dto.zipcode())
                .street(dto.street())
                .country(dto.country())
                .state(dto.state())
                .number(dto.number())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .build();
    }

    public List<AddressResponseDTO> mapAddressToResponse(List<Address> addresses){
        return addresses.stream().map(this::mapToResponse).toList();
    }

}
