package com.systemdelivery.authentication.controller.advice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ErrorResponseDTO(
        Integer status,
        String message,
        LocalDateTime timeStamp,
        List<Map<String, String>> errors) {
}
