package com.deliverysystem.delivery.client.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderRepresentationDTO(
        String id,
        UUID restaurantId,
        LocalDate orderDate,
        BigDecimal total,
        String status,
        String notes,
        LocalDateTime estimated_delivery,
        CustomerRepresentationDTO customer) {
}
