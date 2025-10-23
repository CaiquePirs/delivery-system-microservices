package com.deliverysystem.orders.event.subscriber;

import com.deliverysystem.orders.event.representation.PaymentApprovedEvent;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.model.enums.OrderStatus;
import com.deliverysystem.orders.repository.OrderRepository;
import com.deliverysystem.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventSubscriber {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @RabbitListener(queues = "${spring.rabbitmq.exchange-approved-payment}")
    public void subscriber(PaymentApprovedEvent event){
        try {
            Order order = orderService.findOrderById(event.getOrderId());

            order.setPaymentId(new ObjectId(event.getId()));
            order.setStatus(OrderStatus.PAID);
            order.setUpdated_at(LocalDateTime.now());

            orderRepository.save(order);

        } catch (Exception e){
            log.info("Error updating order information from the approved payment queue, error: {}", e.getMessage());
        }
    }

}
