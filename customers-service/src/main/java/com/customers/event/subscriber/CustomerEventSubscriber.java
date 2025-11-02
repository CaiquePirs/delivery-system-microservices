package com.customers.event.subscriber;

import com.customers.controller.dto.CustomerRequestDTO;
import com.customers.event.representation.CustomerEventResponse;
import com.customers.event.representation.RegisterEventStatus;
import com.customers.mapper.CustomerMapper;
import com.customers.model.Customer;
import com.customers.repository.CustomerRepository;
import com.customers.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventSubscriber {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${CUSTOMERS_CREATE_QUEUE}")
    public String subscriberInNewCustomerCreated(String messageJson) {
        try {
            CustomerRequestDTO customerRequest = objectMapper.readValue(messageJson, CustomerRequestDTO.class);
            Customer customer = customerService.createCustomer(customerRequest);
            CustomerEventResponse response = customerMapper.mapToEvent(customer);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            try {
                return objectMapper.writeValueAsString(
                        CustomerEventResponse.builder()
                                .status(RegisterEventStatus.ERROR)
                                .build()
                );
            } catch (Exception ex) {
                return "{\"status\":\"ERROR\"}";
            }
        }
    }

    @RabbitListener(queues = "${CUSTOMERS_ERROR_CREATE_QUEUE}")
    public void subscriberInErrorCustomerCreated(String customerId) {
        try {
            customerService.deleteCustomerById(UUID.fromString(customerId));

        } catch (Exception e){
            log.error("Error when queuing to delete the customer with ID: {}", customerId);
        }
    }
}