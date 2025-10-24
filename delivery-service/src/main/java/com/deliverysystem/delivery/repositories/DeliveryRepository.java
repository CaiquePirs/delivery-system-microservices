package com.deliverysystem.delivery.repositories;

import com.deliverysystem.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
}
