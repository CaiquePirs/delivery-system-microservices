package com.deliverysystem.restaurants.controller.advice.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponseDTO(
        Integer status,
        String message,
        LocalDateTime timeStamp,
        List<ErrorMessageDTO> errors) {
}
