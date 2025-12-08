package com.deliverysystem.orders.calculator;

import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.service.calculator.OrderCalculator;
import com.deliverysystem.orders.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderCalculatorTest {

    private OrderCalculator orderCalculator;

    @BeforeEach
    void setUp(){
        orderCalculator = new OrderCalculator();
    }

    @Test
    void shouldCalculateTotalItemsOrdersSuccessfully(){
        List<ItemsOrder> itemsOrders = TestUtils.mockItemsOrders();

        BigDecimal totalItemsOrder = Assertions.assertDoesNotThrow(
                () -> orderCalculator.calculateTotalOrder(itemsOrders)
        );

        assertNotNull(totalItemsOrder);
        assertEquals(BigDecimal.valueOf(90.00), totalItemsOrder);
    }

    @Test
    void shouldReturnZeroWhenItemsOrderIsEmpty(){
        List<ItemsOrder> itemsOrders = List.of();

        BigDecimal totalItemsOrder = Assertions.assertDoesNotThrow(
                () -> orderCalculator.calculateTotalOrder(itemsOrders)
        );

        assertTrue(itemsOrders.isEmpty());
        assertEquals(BigDecimal.ZERO, totalItemsOrder);
    }
}
