package com.systemdelivery.authentication.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().getHeaders().remove("__TypeId__");
            return message;
        });
        return template;
    }

    @Bean
    Queue customerDeletedQueue(){
        return new Queue("customer-deleted-queue", true);
    }

    @Bean
    Queue restaurantDeletedQueue(){
        return new Queue("restaurant-deleted-queue");
    }
}
