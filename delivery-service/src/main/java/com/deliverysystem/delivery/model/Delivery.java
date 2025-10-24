package com.deliverysystem.delivery.model;

import com.deliverysystem.delivery.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_deliveries")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;

    @Column(name = "delivery_tax", nullable = false, precision = 19, scale = 2)
    private BigDecimal deliveryTax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DeliveryStatus status;

    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;

    @Column(name = "estimated_delivery_time", nullable = false)
    private LocalDateTime estimatedDeliveryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currier_id")
    private Currier currier;

    @Embedded
    private Address deliveryAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}