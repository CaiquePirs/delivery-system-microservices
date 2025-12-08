package com.deliverysystem.orders.utils;

import com.deliverysystem.orders.client.representation.CustomerDTO;
import com.deliverysystem.orders.client.representation.DeliveryAddressDTO;
import com.deliverysystem.orders.client.representation.MenuDTO;
import com.deliverysystem.orders.client.representation.RestaurantDTO;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.Order;
import com.deliverysystem.orders.model.enums.AuditStatus;
import com.deliverysystem.orders.model.enums.OrderStatus;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    public static List<ItemsOrder> mockItemsOrders(){
        return List.of(new ItemsOrder(
                        UUID.randomUUID().toString(),
                        2,
                        BigDecimal.valueOf(90.00),
                        mockRestaurant().menus().get(0).id()
                )
        );
    }

    public static DeliveryAddressDTO mockAddress(){
        return new DeliveryAddressDTO(
                UUID.randomUUID(),
                "3 Bridge Street",
                "33th",
                "X345Y890",
                "Connel's",
                "Waterford",
                "Waterford",
                "Ireland"
        );
    }

    public static CustomerDTO mockCustomer(){
        return new CustomerDTO(
                UUID.randomUUID(),
                "Customer",
                "customer@gmail.com",
                "(99) 99287-8767",
                List.of(mockAddress())
        );
    }

    public static RestaurantDTO mockRestaurant(){
        return new RestaurantDTO(
                UUID.randomUUID(),
                "Italian Food",
                "italianfood@gmail.com",
                "italianfood.com",
                "Italian Food Restaurant",
                "OPEN",
                mockAddress(),
                List.of(menuDTO())
        );
    }

    public static MenuDTO menuDTO(){
        return new MenuDTO(
                UUID.randomUUID(),
                "Italian Food",
                BigDecimal.valueOf(45.00),
                "FOOD"
        );
    }

    public static Order mockOrder(){
        return Order.builder()
                .id(new ObjectId())
                .notes("Order Notes")
                .status(OrderStatus.PENDING_PAYMENT)
                .orderDate(LocalDate.now())
                .itemsOrder(mockItemsOrders())
                .total(BigDecimal.valueOf(90.00))
                .restaurantId(UUID.randomUUID())
                .deliveryAddressId(UUID.randomUUID())
                .estimatedDelivery(LocalDateTime.now().plusHours(2))
                .auditStatus(AuditStatus.ACTIVE)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
    }
}
