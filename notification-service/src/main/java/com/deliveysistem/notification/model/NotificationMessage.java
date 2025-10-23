package com.deliveysistem.notification.model;

import lombok.Builder;

@Builder
public record NotificationMessage(String subject, String text) {
}
