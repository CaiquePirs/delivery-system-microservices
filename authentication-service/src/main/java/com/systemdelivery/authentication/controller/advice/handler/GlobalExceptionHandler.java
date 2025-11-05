package com.systemdelivery.authentication.controller.advice.handler;

import com.systemdelivery.authentication.controller.advice.dto.ErrorResponseDTO;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<Map<String, String>> listErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error",
                LocalDateTime.now(),
                listErrors
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(ErrorLoginException.class)
    public ResponseEntity<ErrorResponseDTO> handleErrorLoginException(ErrorLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(Map.of("Login Error", e.getMessage()))
                ));
    }

    @ExceptionHandler(ErrorRegisterException.class)
    public ResponseEntity<ErrorResponseDTO> handleErrorRegisterException(ErrorRegisterException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(Map.of("Login Error", e.getMessage()))
                ));
    }

}
