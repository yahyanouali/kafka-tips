package com.example.resource;

import com.example.model.UserDto;
import com.example.repository.UserCacheRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.util.List;

@Path( "/cache")
public class CacheResource {

    @Inject
    UserCacheRepository cacheRepository;

    @GET
    @Produces("application/json")
    @Path("/users")
    public List<UserDto> getUsers() {
        return cacheRepository.readAll();
    }

    @GET
    @Produces("application/json")
    @Path("/user/{name}")
    public UserDto getUser(String name) {
        return cacheRepository.read(name);
    }

}
