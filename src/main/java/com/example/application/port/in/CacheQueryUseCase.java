package com.example.application.port.in;

import com.example.model.UserDto;

import java.util.List;

/**
 * Inbound port for querying cached users.
 */
public interface CacheQueryUseCase {
    List<UserDto> listUsers();
    UserDto getUser(String name);
}
