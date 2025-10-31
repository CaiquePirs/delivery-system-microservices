package com.systemdelivery.authentication.event.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.CreateCustomerRequestDTO;
import com.systemdelivery.authentication.event.representation.CustomerEventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${CUSTOMERS_CREATE_QUEUE}")
    private String CUSTOMERS_CREATE_QUEUE;

    public CustomerEventResponse publishCustomerCreate(CreateCustomerRequestDTO customerDTO) {
        try {
            String json = objectMapper.writeValueAsString(customerDTO);
            String jsonResponse = (String) rabbitTemplate.convertSendAndReceive(
                    CUSTOMERS_CREATE_QUEUE,
                    json
            );
            return objectMapper.readValue(jsonResponse, CustomerEventResponse.class);

        } catch (Exception e) {
            log.error("Error while publishing customer create", e);
            throw new ErrorRegisterException("Error registering customer");
        }
    }
}
