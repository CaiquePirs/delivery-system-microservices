package com.deliveysistem.notification_service.strategy;

import com.deliveysistem.notification.generator.EmailNotificationTemplateGenerator;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.enums.NotificationType;
import com.deliveysistem.notification.strategy.NotificationStrategy;
import com.deliveysistem.notification.strategy.factory.NotificationFactory;
import com.deliveysistem.notification.strategy.impl.SendNotificationViaEmail;
import com.deliveysistem.notification_service.utils.TestUtils;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NotificationFactoryTest {

    @Mock private SendNotificationViaEmail sendNotificationViaEmail;
    private NotificationFactory notificationFactory;

    @BeforeEach
    void setUp() {
        List<NotificationStrategy> strategies = List.of(sendNotificationViaEmail);
        notificationFactory = new NotificationFactory(strategies);
    }

    @Test
    void shouldSendNotificationByEmailSuccessfully() {
        Notification notification = Notification.builder()
                .recipients(List.of(TestUtils.customerRecipient()))
                .body(TestUtils.orderEvent())
                .message(TestUtils.notificationMessage())
                .notificationTypes(List.of(NotificationType.EMAIL))
                .build();

        notificationFactory.send(notification);

        verify(sendNotificationViaEmail, times(1)).send(notification);
    }
}

