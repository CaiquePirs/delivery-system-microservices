package com.deliveysistem.notification_service.strategy;

import com.deliveysistem.notification.generator.EmailNotificationTemplateGenerator;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.enums.NotificationType;
import com.deliveysistem.notification.strategy.impl.SendNotificationViaEmail;
import com.deliveysistem.notification_service.utils.TestUtils;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
class SendNotificationViaEmailTest {

    @Mock private JavaMailSender emailSender;
    @Spy private EmailNotificationTemplateGenerator emailNotificationTemplateGenerator;
    @InjectMocks private SendNotificationViaEmail sendNotificationViaEmail;

    private String emailAddress = "deliverySystem@gmail.com";

    @BeforeEach
    void injectEmailAddress() throws Exception {
        var field = SendNotificationViaEmail.class.getDeclaredField("emailAddress");
        field.setAccessible(true);
        field.set(sendNotificationViaEmail, emailAddress);
    }

    @Test
    void shouldSendNotificationViaEmailSuccessfully() throws Exception {
        Notification notification = Notification.builder()
                .body(TestUtils.orderEvent())
                .notificationTypes(List.of(NotificationType.EMAIL))
                .recipients(List.of(TestUtils.customerRecipient()))
                .message(TestUtils.notificationMessage())
                .build();

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> sendNotificationViaEmail.send(notification));

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }
}
