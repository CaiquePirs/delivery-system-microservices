package com.deliverysystem.delivery.service.calculator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DeliveryTaxCalculator {

    public BigDecimal calculateDeliveryTax(BigDecimal totalOrderAmount) {
        BigDecimal taxRate;

        if (totalOrderAmount.compareTo(BigDecimal.valueOf(50)) < 0) {
            taxRate = BigDecimal.valueOf(0.30);
        } else if (totalOrderAmount.compareTo(BigDecimal.valueOf(200)) < 0) {
            taxRate = BigDecimal.valueOf(0.20);
        } else {
            taxRate = BigDecimal.valueOf(0.10);
        }

        BigDecimal deliveryTax = totalOrderAmount.multiply(taxRate);
        return deliveryTax.setScale(2, RoundingMode.HALF_UP);
    }

}
