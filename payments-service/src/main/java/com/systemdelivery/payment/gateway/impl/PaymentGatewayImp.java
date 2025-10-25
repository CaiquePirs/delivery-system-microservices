package com.systemdelivery.payment.gateway.impl;

import com.systemdelivery.payment.gateway.PaymentGateway;
import com.systemdelivery.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayImp implements PaymentGateway {

    private final SimulatedGateway simulatedGateway;

    @Override
    public void pay(Payment payment) {
        simulatedGateway.simulateCallback(payment.getId().toString());
    }
}
