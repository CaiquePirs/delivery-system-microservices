package com.systemdelivery.payment.model;

import com.systemdelivery.payment.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "payments_db")
public class Payment {

    @MongoId
    private ObjectId id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentData paymentData;
    private String notes;
    private String paymentCode;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
