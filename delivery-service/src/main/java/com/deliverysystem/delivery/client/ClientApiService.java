package com.deliverysystem.delivery.client;

import com.deliverysystem.delivery.client.api.OrderClientApi;
import com.deliverysystem.delivery.client.representation.OrderRepresentationDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientApiService {

    private final OrderClientApi orderClientApi;

    public OrderRepresentationDTO findById(String orderId) {
        try {
            var orderResponse = orderClientApi.findById(orderId);
            return orderResponse.getBody();

        } catch (FeignException e){
            log.error("Error fetching order with ID {}: {}", orderId, e.getMessage());
            return null;
        }
    }
}
