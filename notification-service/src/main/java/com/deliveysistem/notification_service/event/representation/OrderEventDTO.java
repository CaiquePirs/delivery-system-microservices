package com.deliveysistem.notification_service.event.representation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderEventDTO(
        String id,
        UUID restaurantId,
        LocalDate orderDate,
        BigDecimal total,
        String status,
        String notes,
        PaymentDataEventDTO paymentData,
        LocalDateTime estimated_delivery,
        List<ItemsOrderEventDTO> items,
        CustomerEventDTO customer) {
}
