package com.deliverysystem.delivery.repositories;

import com.deliverysystem.delivery.model.Currier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CurrierRepository extends JpaRepository<Currier, UUID> {
    Optional<Currier> findByEmail(String email);
}
