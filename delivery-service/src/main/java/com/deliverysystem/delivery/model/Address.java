package com.deliverysystem.delivery.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Embeddable
@Builder
public class Address {
    private String street;
    private String number;
    private String zipcode;
    private String neighborhood;
    private String city;
    private String state;
    private String country;
}
