package com.deliverysystem.orders.controller.advice.handler;

import com.deliverysystem.orders.controller.advice.dto.ErrorMessageDTO;
import com.deliverysystem.orders.controller.advice.dto.ErrorResponseDTO;
import com.deliverysystem.orders.controller.exception.ClientNotFoundException;
import com.deliverysystem.orders.controller.exception.OrderNotFoundException;
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

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleClientNotFound(ClientNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NO_CONTENT.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(new ErrorMessageDTO("Not Found", e.getMessage()))
                ));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderNotFound(OrderNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NO_CONTENT.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(new ErrorMessageDTO("Not Found", e.getMessage()))
                ));
    }
}
