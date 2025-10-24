package com.deliveysistem.notification.strategy.impl;

import com.deliveysistem.notification.generator.EmailNotificationTemplateGenerator;
import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.Recipient;
import com.deliveysistem.notification.model.enums.NotificationType;
import com.deliveysistem.notification.strategy.NotificationStrategy;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendNotificationViaEmail implements NotificationStrategy {

    private final JavaMailSender emailSender;
    private final EmailNotificationTemplateGenerator emailTemplateGenerator;

    @Value("${EMAIL_ADDRESS}")
    private String emailAddress;

    @Override
    public void send(Notification notification) {
        if (notification.getNotificationType().equals(NotificationType.EMAIL)) {
            for (Recipient recipient : notification.getRecipients()) {
                try {
                    String htmlTemplate = emailTemplateGenerator.generateTemplateForRecipient(recipient, notification);

                    if (htmlTemplate == null) {
                        log.warn("No email template found for recipient type: {}", recipient.getType());
                        continue;
                    }

                    MimeMessage mimeMessage = emailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

                    helper.setSubject(notification.getMessage().subject());
                    helper.setTo(recipient.getEmail());
                    helper.setText(htmlTemplate, true);
                    helper.setFrom(emailAddress);

                    emailSender.send(mimeMessage);
                } catch (Exception e) {
                    log.error("Error sending email to {}: {}", recipient.getEmail(), e.getMessage(), e);
                }
            }
        }
    }
}
