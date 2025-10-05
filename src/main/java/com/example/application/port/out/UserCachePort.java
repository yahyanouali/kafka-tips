package com.example.application.port.out;

import com.example.avro.User;
import com.example.model.UserDto;

import java.util.List;

/**
 * Outbound port for user cache operations.
 * Implemented by infrastructure adapters (e.g., Redis repository).
 */
public interface UserCachePort {
    void save(User user);
    UserDto read(String key);
    List<UserDto> readAll();
}
