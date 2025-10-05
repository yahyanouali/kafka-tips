package com.example.application.service;

import com.example.application.port.in.CacheQueryUseCase;
import com.example.application.port.out.UserCachePort;
import com.example.model.UserDto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;

/**
 * Application service implementing cache query use cases.
 */
@ApplicationScoped
public class CacheQueryService implements CacheQueryUseCase {

    private final UserCachePort cachePort;

    public CacheQueryService(UserCachePort cachePort) {
        this.cachePort = Objects.requireNonNull(cachePort);
    }

    @Override
    public List<UserDto> listUsers() {
        return cachePort.readAll();
    }

    @Override
    public UserDto getUser(String name) {
        return cachePort.read(name);
    }
}
