package com.deliverysystem.delivery.advice.handler;

import com.deliverysystem.delivery.advice.dtos.ErrorMessageDTO;
import com.deliverysystem.delivery.advice.dtos.ErrorResponseDTO;
import com.deliverysystem.delivery.advice.exceptions.CurrierFoundException;
import com.deliverysystem.delivery.advice.exceptions.CurrierNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorMessageDTO> listErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorMessageDTO(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error",
                LocalDateTime.now(),
                listErrors
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(CurrierFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCurrierFound(CurrierFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(new ErrorMessageDTO("Currier found", e.getMessage()))
                ));
    }

    @ExceptionHandler(CurrierNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCurrierNotFound(CurrierNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(new ErrorMessageDTO("Currier not found", e.getMessage()))
                ));
    }
}
