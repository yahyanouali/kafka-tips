package com.example.resource;

import com.example.application.port.in.CacheQueryUseCase;
import com.example.model.UserDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import java.util.List;

@Path("/cache")
public class CacheResource {

    @Inject
    CacheQueryUseCase cacheQuery;

    @GET
    @Produces("application/json")
    @Path("/users")
    public List<UserDto> getUsers() {
        return cacheQuery.listUsers();
    }

    @GET
    @Produces("application/json")
    @Path("/user/{name}")
    public UserDto getUser(@PathParam("name") String name) {
        return cacheQuery.getUser(name);
    }

}
