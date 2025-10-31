package com.systemdelivery.authentication.controller.advice.handler;

import com.systemdelivery.authentication.controller.advice.dto.ErrorResponseDTO;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorLoginException.class)
    public ResponseEntity<ErrorResponseDTO> handleErrorLoginException(ErrorLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(ErrorRegisterException.class)
    public ResponseEntity<ErrorResponseDTO> handleErrorRegisterException(ErrorRegisterException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

}
