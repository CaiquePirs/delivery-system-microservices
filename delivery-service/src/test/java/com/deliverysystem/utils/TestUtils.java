package com.deliverysystem.utils;

import com.deliverysystem.delivery.client.representation.CustomerDTO;
import com.deliverysystem.delivery.client.representation.OrderDTO;
import com.deliverysystem.delivery.model.Address;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.model.Delivery;
import com.deliverysystem.delivery.model.enums.DeliveryStatus;
import com.deliverysystem.delivery.model.enums.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class TestUtils {

    public static Address address(){
        return Address.builder()
                .city("Waterford")
                .country("Ireland")
                .neighborhood("3 Bridge")
                .state("Waterford DC.")
                .number("134e")
                .street("Street")
                .zipcode("XY33K5")
                .build();
    }

    public static CustomerDTO customerDTO(){
        return new CustomerDTO(
                UUID.randomUUID(),
                "Customer",
                "customer@gmail.com",
                "(99) 99999-9999",
                address()
        );
    }

    public static OrderDTO orderDTO(){
        return new OrderDTO(
                UUID.randomUUID().toString(),
                UUID.randomUUID(),
                LocalDate.now(),
                BigDecimal.valueOf(200.00),
                "PAID",
                "No picles",
                LocalDateTime.now().plusHours(1),
                customerDTO()
        );
    }

    public static Delivery deliveryAssigned(){
        return Delivery.builder()
                .orderId(UUID.randomUUID().toString())
                .totalOrderAmount(BigDecimal.valueOf(200.00))
                .status(DeliveryStatus.ASSIGNED)
                .deliveryAddress(customerDTO().deliveryAddress())
                .estimatedDeliveryTime(LocalDateTime.now().plusHours(2))
                .build();
    }

    public static Currier currier(){
        return Currier.builder().name("Currier Example")
                .email(String.format("currierExample_%s_@gmail.com", UUID.randomUUID()))
                .phone("+1234567890")
                .vehicleType(VehicleType.MOTORCYCLE)
                .completedDeliveries(new ArrayList<>())
                .build();
    }

}
