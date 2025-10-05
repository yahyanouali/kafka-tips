package com.example.application.service;

import com.example.application.port.in.UserCommandUseCase;
import com.example.application.port.out.UserEventPublisherPort;
import com.example.avro.User;
import com.example.model.UserDto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;

/**
 * Application service implementing the user creation use case.
 */
@ApplicationScoped
public class UserCommandService implements UserCommandUseCase {

    private final UserEventPublisherPort publisherPort;

    public UserCommandService(UserEventPublisherPort publisherPort) {
        this.publisherPort = Objects.requireNonNull(publisherPort);
    }

    @Override
    public void createUser(UserDto user) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(user.name(), "user.name must not be null");
        User avro = new User(user.name(), user.age());
        publisherPort.sendUser(avro);
    }
}
