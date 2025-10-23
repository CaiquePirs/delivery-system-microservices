package com.deliveysistem.notification.event.representation;

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
public class OrderEventDTO{

    private String id;
    private UUID restaurantId;
    private LocalDate orderDate;
    private BigDecimal total;
    private String status;
    private String notes;
    private PaymentDataEventDTO paymentData;
    private LocalDateTime estimated_delivery;
    private List<ItemsOrderEventDTO> items;
    private CustomerEventDTO customer;

}
