package com.deliveysistem.notification.strategy.impl;

import com.deliveysistem.notification.model.Notification;
import com.deliveysistem.notification.model.NotificationType;
import com.deliveysistem.notification.generator.EmailTemplateGenerator;
import com.deliveysistem.notification.strategy.NotificationStrategy;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendNotificationViaEmail implements NotificationStrategy {

    private final JavaMailSender emailSender;
    private final EmailTemplateGenerator emailTemplateGenerator;

    @Value("${EMAIL_ADDRESS}")
    private String emailAddress;

    @Override
    public void send(Notification notification) {
        try {
            if (notification.getNotificationType().equals(NotificationType.EMAIL)) {
                MimeMessage mimeMessage = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                String htmlTemplate = emailTemplateGenerator.generate(notification);

                helper.setSubject(notification.getSubject());
                helper.setTo(notification.getTo());
                helper.setText(htmlTemplate, true);
                helper.setFrom(emailAddress);

                emailSender.send(mimeMessage);
            }

        } catch (Exception e) {
            log.error("Error sending HTML email to recipient {}, Error: {}", notification.getTo(), e.getMessage());
        }
    }
}
