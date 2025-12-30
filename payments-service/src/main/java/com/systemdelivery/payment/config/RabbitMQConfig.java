package com.systemdelivery.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
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
    Queue verifyPaymentQueue(){
        return new Queue("verify-payment-queue", true);
    }

    @Bean
    Queue verifyPaymentNotifyQueue(){
        return new Queue("verify-payment-notify-queue", true);
    }

    @Bean
    FanoutExchange verifyPaymentFanout(){
        return new FanoutExchange("verify-payment-fanout");
    }

    @Bean
    Binding bindingVerifyPaymentQueue(){
        return BindingBuilder
                .bind(verifyPaymentQueue())
                .to(verifyPaymentFanout());
    }

    @Bean
    Binding bindingVerifyPaymentNotifyQueue(){
        return BindingBuilder
                .bind(verifyPaymentNotifyQueue())
                .to(verifyPaymentFanout());
    }

    @Bean
    FanoutExchange approvedPaymentFanout(){
        return new FanoutExchange("approved-payment-fanout");
    }

    @Bean
    Queue paymentApprovedDeliveryQueue(){
        return new Queue("payment-approved-delivery-queue", true);
    }

    @Bean
    Queue paymentApprovedNotifyQueue(){
        return new Queue("payment-approved-notify-queue", true);
    }

    @Bean
    Queue paymentApprovedOrderQueue(){
        return new Queue("payment-approved-order-queue", true);
    }

    @Bean
    Binding bindingPaymentApprovedDeliveryQueue(){
        return BindingBuilder
                .bind(paymentApprovedDeliveryQueue())
                .to(approvedPaymentFanout());
    }

    @Bean
    Binding bindingPaymentApprovedNotifyQueue(){
        return BindingBuilder
                .bind(paymentApprovedNotifyQueue())
                .to(approvedPaymentFanout());
    }

    @Bean
    Binding bindingPaymentApprovedOrderQueue(){
      return BindingBuilder
                .bind(paymentApprovedOrderQueue())
                .to(approvedPaymentFanout());
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
    FanoutExchange deliveryReadyFanout(){
        return new FanoutExchange("delivery-ready-fanout");
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
                .bind(deliveryShippedUpdateQueue())
                .to(deliveryReadyFanout());
    }
}
