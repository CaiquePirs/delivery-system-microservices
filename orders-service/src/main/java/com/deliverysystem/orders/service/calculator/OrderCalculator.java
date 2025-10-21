package com.deliverysystem.orders.service.calculator;

import com.deliverysystem.orders.model.ItemsOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderCalculator {

    public BigDecimal calculateTotalOrder(List<ItemsOrder> items){
        return items.stream().map(ItemsOrder::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
