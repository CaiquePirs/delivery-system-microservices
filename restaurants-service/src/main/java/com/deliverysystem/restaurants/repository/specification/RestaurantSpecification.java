package com.deliverysystem.restaurants.repository.specification;

import com.deliverysystem.restaurants.controller.dto.RestaurantQueryFilter;
import com.deliverysystem.restaurants.model.Menu;
import com.deliverysystem.restaurants.model.Restaurant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestaurantSpecification {

    public static Specification<Restaurant> specification(RestaurantQueryFilter filter){
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filter.getRestaurantName() != null && !filter.getRestaurantName().isBlank()){
                predicates.add(cb.equal(root.get("name"), filter.getRestaurantName()));
            }

            if (filter.getMenuType() != null) {
                Join<Restaurant, Menu> join = root.join("menus");
                predicates.add(cb.equal(join.get("menuType"), filter.getMenuType()));
            }

            if(filter.getRestaurantStatus() != null){
                predicates.add(cb.equal(root.get("status"), filter.getRestaurantStatus()));
            }

            if (filter.getMenuPrice() != null) {
                Join<Restaurant, Menu> join = root.join("menus");
                predicates.add(cb.equal(join.get("price"), filter.getMenuPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

}
