package com.systemdelivery.authentication.event.subscriber;

import com.systemdelivery.authentication.event.CustomerDeletedEvent;
import com.systemdelivery.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationEventSubscriber {

    private final AuthenticationService authenticationService;

    @RabbitListener(queues = "${CUSTOMERS_DELETED_QUEUE}")
    public void subscriberInCustomerDeletedEvent(CustomerDeletedEvent event) {
        try {
            authenticationService.disableUserByEmail(event.email());

        } catch (Exception e){
            log.error("Error when subscriber in Customer deleted event: {}", event + "with error: " + e);
        }
    }

}
