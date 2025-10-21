package com.deliverysystem.orders.controller.advice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        Integer status,
        String message,
        LocalDateTime timeStamp,
        List<ErrorMessageDTO> errors
) {
}
