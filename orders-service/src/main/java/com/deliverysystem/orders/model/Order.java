package com.deliverysystem.orders.model;

import com.deliverysystem.orders.model.enums.AuditStatus;
import com.deliverysystem.orders.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
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
    private UUID paymentId;
    private UUID deliveryId;
    private UUID customerId;
    private UUID restaurantId;
    private UUID deliveryAddressId;
    private LocalDate orderDate;
    private BigDecimal total;
    private OrderStatus status;
    private String notes;
    private LocalDateTime estimatedDelivery;
    private List<ItemsOrder> itemsOrder = new ArrayList<>();
    @Transient
    private PaymentData paymentData;
    private AuditStatus auditStatus;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
