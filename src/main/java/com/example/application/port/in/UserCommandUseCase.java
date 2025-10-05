package com.example.application.port.in;

import com.example.model.UserDto;

/**
 * Inbound port representing the use case of creating/enqueuing a user.
 */
public interface UserCommandUseCase {
    void createUser(UserDto user);
}
