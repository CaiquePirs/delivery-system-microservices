package com.deliverysystem.orders.repository;

import com.deliverysystem.orders.model.Order;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface OrderRepository extends MongoRepository<Order, ObjectId>{
    Page<Order> findAllByCustomerId(UUID customerId, Pageable pageable);
}
