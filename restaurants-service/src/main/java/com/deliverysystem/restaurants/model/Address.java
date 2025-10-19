package com.deliverysystem.restaurants.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Embeddable
@Data
public class Address {

    @NotBlank(message = "Street is required")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Number is required")
    @Column(nullable = false)
    private String number;

    @NotBlank(message = "ZipCode is required")
    @Column(nullable = false)
    private String zipcode;

    @NotBlank(message = "Neighborhood is required")
    @Column(nullable = false)
    private String neighborhood;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "Country is required")
    @Column(nullable = false)
    private String country;
}
