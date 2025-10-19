package com.deliverysystem.restaurants.repository;

import com.deliverysystem.restaurants.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
}
