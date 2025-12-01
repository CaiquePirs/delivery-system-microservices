package com.deliverysystem.delivery.calculator;

import com.deliverysystem.delivery.service.calculator.DeliveryTaxCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeliveryTaxCalculatorTest {

    private DeliveryTaxCalculator deliveryTaxCalculator;

    @BeforeEach
    void setUp(){
        deliveryTaxCalculator = new DeliveryTaxCalculator();
    }

    @Test
    @DisplayName("Should calculate delivery tax by R$50.00 successfully ")
    void shouldCalculateDeliveryTaxBy50CalculateSuccessfully(){
        BigDecimal totalOrderAmount = BigDecimal.valueOf(50.00);
        BigDecimal expectedTax = BigDecimal.valueOf(10.00);

        BigDecimal result = deliveryTaxCalculator.calculateDeliveryTax(totalOrderAmount);

        assertNotNull(totalOrderAmount);
        assertEquals(expectedTax, result);
    }

    @Test
    void shouldCalculateDeliveryTaxBy200Successfully(){
        BigDecimal totalOrderAmount = BigDecimal.valueOf(200.00);
        BigDecimal expectedTax = BigDecimal.valueOf(20.00);

        BigDecimal result = deliveryTaxCalculator.calculateDeliveryTax(totalOrderAmount);

        assertNotNull(totalOrderAmount);
        assertNotNull(result);
        assertThat(expectedTax).isEqualByComparingTo(result);
    }

    @Test
    void shouldCalculateDeliveryTaxBy300Successfully(){
        BigDecimal totalOrderAmount = BigDecimal.valueOf(250.00);
        BigDecimal expectedTax = BigDecimal.valueOf(25.00);

        BigDecimal result = deliveryTaxCalculator.calculateDeliveryTax(totalOrderAmount);

        assertNotNull(totalOrderAmount);
        assertNotNull(result);
        assertThat(expectedTax).isEqualByComparingTo(result);
    }

}
