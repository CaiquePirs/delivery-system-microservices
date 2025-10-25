package com.deliverysystem.orders.event.subscriber;

import com.deliverysystem.orders.event.representation.DeliveryReadyEvent;
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
    public void subscriberPaymentApproved(PaymentApprovedEvent event){
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

    @RabbitListener(queues = "${spring.rabbitmq.delivery-ready-update}")
    public void subscriberDeliveryReady(DeliveryReadyEvent event) {
        try {
            log.info("Received DeliveryReadyEvent: {}", event);

            Order order = orderService.findOrderById(event.orderId());

            order.setDeliveryId(event.deliveryId());
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
            order.setUpdated_at(LocalDateTime.now());

            orderRepository.save(order);

            log.info("Order updated successfully for DeliveryReadyEvent: {}", event);

        } catch (Exception e){
            log.info("Error updating order information from the delivery ready queue, error: {}", e.getMessage());
        }
    }


}
