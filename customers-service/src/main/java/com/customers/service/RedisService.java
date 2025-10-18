package com.customers.service;

import com.customers.controller.dto.CustomerResponseDTO;
import com.customers.mapper.CustomerMapper;
import com.customers.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomerMapper customerMapper;

    public CustomerResponseDTO findCustomerInCache(UUID customerId) {
        String accessKey = String.format("customer:%s:cache", customerId);
        return (CustomerResponseDTO) redisTemplate.opsForValue().get(accessKey);
    }

    public void insertCustomerInCache(Customer customer) {
        String accessKey = String.format("customer:%s:cache", customer.getId());
        CustomerResponseDTO customerDTO = customerMapper.mapToResponse(customer);

        redisTemplate.opsForValue().set(accessKey, customerDTO, Duration.ofMinutes(60));
    }
}
