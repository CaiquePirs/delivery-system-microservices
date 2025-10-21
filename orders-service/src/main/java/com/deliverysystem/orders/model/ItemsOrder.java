package com.deliverysystem.orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemsOrder {

    private String id;
    private Integer quantity;
    private BigDecimal total;
    private UUID menuId;
}
