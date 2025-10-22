package com.systemdelivery.payment.model;

import com.systemdelivery.payment.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentData {
    private String data;
    private PaymentMethod method;
    private String paymentCode;
}
