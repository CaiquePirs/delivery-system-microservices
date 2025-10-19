package com.deliverysystem.restaurants.controller.advice.handler;

import com.deliverysystem.restaurants.controller.advice.dto.ErrorMessageDTO;
import com.deliverysystem.restaurants.controller.advice.dto.ErrorResponseDTO;
import com.deliverysystem.restaurants.controller.advice.exceptions.MenuNotFoundException;
import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantFoundException;
import com.deliverysystem.restaurants.controller.advice.exceptions.RestaurantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponseDTO errorResponse(Integer status, String message, List<ErrorMessageDTO> errorMessageDTOS){
        return ErrorResponseDTO.builder()
                .status(status)
                .message(message)
                .timeStamp(LocalDateTime.now())
                .errors(errorMessageDTOS)
                .build();
    }

    @ExceptionHandler(RestaurantFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRestaurantFound(RestaurantFoundException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage(),
                        List.of(new ErrorMessageDTO("Conflict Restaurant", e.getMessage()))
                ));
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRestaurantNotFound(RestaurantNotFoundException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                List.of(new ErrorMessageDTO("Restaurant not found", e.getMessage()))
        ));
    }

    @ExceptionHandler(MenuNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlerMenuNotFound(MenuNotFoundException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                List.of(new ErrorMessageDTO("Menu not found", e.getMessage()))
        ));
    }
}
