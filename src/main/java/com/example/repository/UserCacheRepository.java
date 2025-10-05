package com.example.repository;

import com.example.model.UserDto;
import com.example.avro.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Redis-backed repository to cache users.
 */
@ApplicationScoped
public class UserCacheRepository {

    private static final Logger log = LoggerFactory.getLogger(UserCacheRepository.class);

    private final ValueCommands<String, String> redisValueCommands;

    private final ObjectMapper objectMapper;

    public UserCacheRepository(RedisDataSource redis, ObjectMapper objectMapper) {
        this.redisValueCommands = redis.value(String.class);
        this.objectMapper = objectMapper;
    }

    public void save(User user) {
        if (user == null || user.getName() == null) {
            return;
        }
        String key = user.getName();
        try {
            String value = objectMapper.writeValueAsString(user);
            redisValueCommands.set(key, value);
            log.info("Stored user in Redis key={} value={}", key, value);
        } catch (Exception e) {
            log.error("Failed to store user in Redis for key=" + key, e);
        }
    }
    
    public UserDto read(String key) {
        try {
            String value = redisValueCommands.get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, UserDto.class);
        } catch (Exception e) {
            log.error("Failed to read user from Redis for key=" + key, e);
            return null;
        }
    }
    
    public List<UserDto> readAll() {
        try {
            return redisValueCommands.getDataSource().key()
                    .keys("*")
                    .stream()
                    .map(this::read)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to read all users from Redis", e);
            return List.of();
        }
    }  
    
}
