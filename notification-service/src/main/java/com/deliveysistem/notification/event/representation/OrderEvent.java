package com.deliveysistem.notification.event.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEvent {

    private String id;
    private UUID restaurantId;
    private String restaurantEmail;
    private LocalDate orderDate;
    private BigDecimal total;
    private String status;
    private String notes;
    private LocalDateTime estimated_delivery;
    private List<ItemsOrderEvent> items;
    private CustomerEventDTO customer;

}
