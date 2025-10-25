package com.systemdelivery.payment.gateway;

import com.systemdelivery.payment.model.Payment;

public interface PaymentGateway {
    void pay(Payment payment);
}
