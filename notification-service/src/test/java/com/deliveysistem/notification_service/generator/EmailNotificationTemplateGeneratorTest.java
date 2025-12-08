package com.deliveysistem.notification_service.generator;

import com.deliveysistem.notification.event.representation.OrderEvent;
import com.deliveysistem.notification.generator.EmailNotificationTemplateGenerator;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.Recipient;
import com.deliveysistem.notification.model.enums.NotificationType;
import com.deliveysistem.notification_service.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationTemplateGeneratorTest {

    private EmailNotificationTemplateGenerator emailNotificationTemplateGenerator;
    private Recipient customerRecipient;
    private Recipient restaurantRecipient;
    private OrderEvent orderEvent;

    @BeforeEach
    void setUp(){
        customerRecipient = TestUtils.customerRecipient();
        restaurantRecipient = TestUtils.restaurantRecipient();
        orderEvent = TestUtils.orderEvent();

        emailNotificationTemplateGenerator = new EmailNotificationTemplateGenerator();
    }

    @Test
    void shouldGenerateNotificationTemplateByCustomerRecipient(){
        Notification notification = Notification.builder()
                .notificationTypes(List.of(NotificationType.EMAIL))
                .recipients(List.of(customerRecipient))
                .body(orderEvent)
                .message(TestUtils.notificationMessage())
                .build();

        String templateHtmlByCustomer = assertDoesNotThrow(
                () -> emailNotificationTemplateGenerator.generateTemplateForRecipient(customerRecipient, notification)
        );

        assertFalse(templateHtmlByCustomer.isEmpty());
        assertNotNull(templateHtmlByCustomer);
    }

    @Test
    void shouldShouldGenerateNotificationTemplateByRestaurantRecipient(){
        Notification notification = Notification.builder()
                .notificationTypes(List.of(NotificationType.EMAIL))
                .recipients(List.of(restaurantRecipient))
                .body(orderEvent)
                .message(TestUtils.notificationMessage())
                .build();

        String templateHtmlByRestaurant = assertDoesNotThrow(
                () -> emailNotificationTemplateGenerator.generateTemplateForRecipient(restaurantRecipient, notification)
        );

        assertFalse(templateHtmlByRestaurant.isEmpty());
        assertNotNull(templateHtmlByRestaurant);
    }

    @Test
    void shouldThrowExceptionOrderInformationIsNull(){
        Notification notification = Notification.builder()
                .notificationTypes(List.of(NotificationType.EMAIL))
                .recipients(List.of(restaurantRecipient))
                .message(TestUtils.notificationMessage())
                .build();

        assertThrows(
                Exception.class,
                () -> emailNotificationTemplateGenerator.generateTemplateForRecipient(restaurantRecipient, notification)
        );
    }

}
