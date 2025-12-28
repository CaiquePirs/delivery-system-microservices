package com.deliverysystem.delivery.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
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
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
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
    FanoutExchange deliveryReadyFanout(){
        return new FanoutExchange("delivery-ready-fanout");
    }

    @Bean
    Queue deliveryShippedUpdateQueue(){
        return new Queue("delivery-shipped-update-queue", true);
    }

    @Bean
    Queue deliveryReadyNotifyQueue(){
        return new Queue("delivery-ready-notify-queue", true);
    }

    @Bean
    Binding bindingDeliveryReadyNotifyQueue(){
        return BindingBuilder
                .bind(deliveryReadyNotifyQueue())
                .to(deliveryReadyFanout());
    }

    @Bean
    Binding bindingDeliveryShippedUpdateQueue(){
        return BindingBuilder
                .bind(deliveryReadyNotifyQueue())
                .to(deliveryReadyFanout());
    }
}
