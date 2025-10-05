package com.example.repository;

import com.example.avro.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Redis-backed repository to cache users.
 */
@ApplicationScoped
public class UserCacheRepository {

    private static final Logger log = LoggerFactory.getLogger(UserCacheRepository.class);

    private final ValueCommands<String, String> values;
    private final ObjectMapper objectMapper;

    public UserCacheRepository(RedisDataSource redis, ObjectMapper objectMapper) {
        this.values = redis.value(String.class);
        this.objectMapper = objectMapper;
    }

    public void save(User user) {
        if (user == null || user.getName() == null) {
            return;
        }
        String key = user.getName();
        try {
            String value = objectMapper.writeValueAsString(user);
            values.set(key, value);
            log.info("Stored user in Redis key={} value={}", key, value);
        } catch (Exception e) {
            log.error("Failed to store user in Redis for key=" + key, e);
        }
    }
}
