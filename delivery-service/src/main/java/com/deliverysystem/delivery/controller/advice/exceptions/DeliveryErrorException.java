package com.deliverysystem.delivery.controller.advice.exceptions;

import com.deliverysystem.delivery.model.enums.DeliveryStatus;

import java.util.UUID;

public class DeliveryErrorException extends RuntimeException {
    public DeliveryErrorException(UUID deliveryId, DeliveryStatus status) {
        super(String.format("Error processing delivery ID: %s , the current delivery status is: %s", deliveryId, status));
    }
}
