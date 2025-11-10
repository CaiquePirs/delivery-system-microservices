package com.systemdelivery.authentication.service;

import com.systemdelivery.authentication.controller.dto.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public LoginResponseDTO findUserTokenInCache(String key) {
        String accessKey = String.format("access_token:%s", key);
        return (LoginResponseDTO) redisTemplate.opsForValue().get(accessKey);
    }

    public void insertUserTokenInCache(String key, LoginResponseDTO loginResponse) {
        String accessKey = String.format("access_token:%s", key);
        redisTemplate.opsForValue().set(accessKey, loginResponse, Duration.ofMinutes(29));
    }

    public void removerUserTokenFromCache(String email){
        String accessKey = String.format("access_token:%s", email);
        redisTemplate.delete(accessKey);
    }

}
