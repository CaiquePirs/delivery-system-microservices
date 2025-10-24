package com.deliverysystem.delivery.advice.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        Integer status,
        String message,
        LocalDateTime timestamp,
        List<ErrorMessageDTO> errors ){
}
