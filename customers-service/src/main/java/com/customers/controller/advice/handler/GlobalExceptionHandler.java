package com.customers.controller.advice.handler;

import com.customers.controller.advice.dto.ErrorMessageDTO;
import com.customers.controller.advice.dto.ErrorResponseDTO;
import com.customers.controller.advice.exceptions.CustomerFoundException;
import com.customers.controller.advice.exceptions.NotFoundException;
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

    @ExceptionHandler(CustomerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomerFound(CustomerFoundException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(new ErrorMessageDTO("Customer Found", e.getMessage()))
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomerNotFound(NotFoundException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        List.of(new ErrorMessageDTO("Customer not Found", e.getMessage()))
                ));
    }

}
