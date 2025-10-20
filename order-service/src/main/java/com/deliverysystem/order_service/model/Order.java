package com.deliverysystem.order_service.model;

import com.deliverysystem.order_service.model.enums.AuditStatus;
import com.deliverysystem.order_service.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "orders_db")
public class Order {

    @MongoId
    private ObjectId id;
    private LocalDate orderDate;
    private BigDecimal total;
    private UUID paymentId;
    private UUID deliveryId;
    private UUID customerId;
    private UUID restaurantId;
    private OrderStatus status;
    private String notes;
    private AuditStatus auditStatus;
    private LocalDateTime estimated_delivery;
    private List<ItemsOrder> itemsOrder = new ArrayList<>();
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
}
