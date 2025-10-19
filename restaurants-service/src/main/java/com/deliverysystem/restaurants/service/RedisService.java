package com.deliverysystem.restaurants.service;

import com.deliverysystem.restaurants.controller.dto.RestaurantResponseDTO;
import com.deliverysystem.restaurants.mapper.RestaurantMapper;
import com.deliverysystem.restaurants.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RestaurantMapper restaurantMapper;

    public RestaurantResponseDTO findRestaurantInCache(UUID id){
        String accessKey = String.format("restaurants:%s", id);
        return (RestaurantResponseDTO) redisTemplate.opsForValue().get(accessKey);
    }

    public void insertRestaurantInCache(Restaurant restaurant){
        String accessKey = String.format("restaurants:%s", restaurant.getId());
        RestaurantResponseDTO restaurantDTO = restaurantMapper.toResponse(restaurant);
        redisTemplate.opsForValue().set(accessKey, restaurantDTO, Duration.ofMinutes(60));
    }

}
