package com.deliverysystem.orders.model;

import com.deliverysystem.orders.model.enums.PaymentMethod;

public record PaymentData(String data, PaymentMethod method) {
}
