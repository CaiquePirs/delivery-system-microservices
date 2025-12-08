package com.deliveysistem.notification_service.utils;

import com.deliveysistem.notification.event.representation.*;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.NotificationMessage;
import com.deliveysistem.notification.model.Recipient;
import com.deliveysistem.notification.model.enums.RecipientType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    public static OrderEvent orderEvent(){
        return OrderEvent.builder()
                .id(UUID.randomUUID().toString())
                .customer(customerEventDTO())
                .estimated_delivery(LocalDateTime.now().plusHours(2))
                .notes("No pickles")
                .items(List.of(new ItemsOrderEvent(
                        UUID.randomUUID().toString(),
                        2,
                        BigDecimal.valueOf(50.00),
                        UUID.randomUUID()
                )))
                .restaurantEmail("pizzaplaza@gmail.com")
                .orderDate(LocalDate.now())
                .status("PAID")
                .total(BigDecimal.valueOf(50.00))
                .build();
    }

    public static CustomerEventDTO customerEventDTO(){
        return new CustomerEventDTO(
                UUID.randomUUID(),
                "Customer",
                "customer@gmail.com",
                "(44) 9999-9999",
                new AddressEvent(
                        UUID.randomUUID(),
                        "3 Bridge Street",
                        "23e",
                        "9999-9999",
                        "Waterford",
                        "Waterford",
                        "Waterford",
                        "Ireland"
                )
        );
    }

    public static NotificationMessage notificationMessage(){
        return NotificationMessage.builder()
                .subject("Order confirmed ✅")
                .text("has been successfully received on")
                .build();
    }

    public static Recipient customerRecipient(){
        return new Recipient(
                "customer@gmail.com",
                RecipientType.CUSTOMER
        );
    }

    public static Recipient restaurantRecipient(){
        return Recipient.builder()
                .email("pizzaplaza@gmail.com")
                .type(RecipientType.RESTAURANT)
                .build();
    }

    public static PaymentConfirmedEvent paymentConfirmedEvent(){
        return new PaymentConfirmedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                BigDecimal.valueOf(200.00),
                "AUTHORIZED"
        );
    }

    public static DeliveryReadyEvent deliveryReadyEvent(){
        return new DeliveryReadyEvent(
                UUID.randomUUID(),
                UUID.randomUUID().toString()
        );
    }


}
