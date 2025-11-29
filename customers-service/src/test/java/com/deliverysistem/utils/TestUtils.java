package com.deliverysistem.utils;

import com.customers.controller.dto.AddressRequestDTO;
import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.model.Address;
import com.customers.model.Customer;
import com.customers.model.enums.AuditStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class TestUtils {

    public static AddressRequestDTO addressRequestDTO(){
        return new AddressRequestDTO(
                "Street",
                "19",
                "9999999",
                "Sams Park",
                "NY",
                "NY",
                "USA"
        );
    }

    public static Address address(){
        return new Address(
                UUID.randomUUID(),
                "Street",
                "14e",
                "",
                "",
                "Waterford",
                "Waterford",
                "Ireland",
                new Customer(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Customer customer(){
        return Customer.builder()
                .id(UUID.randomUUID())
                .name("Customer")
                .phone("(99) 999999")
                .email("customer@gmail.com")
                .status(AuditStatus.ACTIVE)
                .addresses(new ArrayList<>())
                .build();
    }

    public static CustomerRequestDTO customerRequestDTO(){
        return new CustomerRequestDTO(
                "Customer",
                "customer@gmail.com",
                "(00) 0000000",
                new AddressRequestDTO(
                        "Street",
                        "14e",
                        "99999999",
                        "3 bridge street",
                        "Waterford",
                        "Waterford",
                        "Ireland"
                )
        );
    }

}
