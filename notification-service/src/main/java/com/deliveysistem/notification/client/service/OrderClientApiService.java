package com.deliveysistem.notification.client.service;

import com.deliveysistem.notification.client.api.OrdersClientApi;
import com.deliveysistem.notification.event.representation.OrderEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderClientApiService {

    private final OrdersClientApi ordersClientApi;

    public OrderEventDTO findOrderById(String orderId){
        try {
            var response = ordersClientApi.findOrderById(orderId);
            return response.getBody();

        } catch (Exception e){
            log.error("Error when find orderId: {}", orderId);
            return null;
        }
    }

}
