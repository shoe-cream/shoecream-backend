package com.springboot.auth.service;


import com.springboot.auth.jwt.JwtTokenizer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final JwtTokenizer jwtTokenizer;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthService(JwtTokenizer jwtTokenizer, RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenizer = jwtTokenizer;
        this.redisTemplate = redisTemplate;
    }
    public boolean logout(String username) {
        return jwtTokenizer.deleteRegisterToken(username); // JwtTokenizer를 사용하여 저장된 토큰을 삭제합니다.
    }
}
