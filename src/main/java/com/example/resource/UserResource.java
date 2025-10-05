package com.example.resource;

import com.example.UserDto;
import com.example.service.UserProducerService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource that accepts user creation requests and publishes them to Kafka.
 */
@Path("/users")
public class UserResource {

    private final UserProducerService producerService;

    public UserResource(UserProducerService producerService) {
        this.producerService = producerService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(UserDto user) {
        if (user == null || user.name() == null || user.name().isBlank() || user.age() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid user payload: 'name' must be non-blank and 'age' must be non-negative.")
                    .build();
        }
        producerService.publishUser(user);
        return Response.accepted().entity("User enqueued").build();
    }
}
