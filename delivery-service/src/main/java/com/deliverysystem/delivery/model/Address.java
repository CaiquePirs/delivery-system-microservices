package com.deliverysystem.delivery.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class Address {

    private UUID id;
    private String street;
    private String number;
    private String zipcode;
    private String neighborhood;
    private String city;
    private String state;
    private String country;
}
