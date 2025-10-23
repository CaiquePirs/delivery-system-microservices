package com.deliverysystem.orders.client.api;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "AddressClient", url = "${addresses.url}")
public interface AddressClientApi {

    @GetMapping("/{id}")
    ResponseEntity<AddressRepresentationDTO> findAddressById(
            @PathVariable("id") UUID addressId);
}
