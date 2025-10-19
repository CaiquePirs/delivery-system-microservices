package com.deliverysystem.restaurants.repository;

import com.deliverysystem.restaurants.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Optional<Restaurant> findByEmail(String email);
}
