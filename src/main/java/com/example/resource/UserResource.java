package com.example.resource;

import com.example.UserDto;
import com.example.avro.User;
import com.example.service.UserProducerService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path( "/users")
public class UserResource {

    private final UserProducerService producer;

    public UserResource(UserProducerService producer) {
        this.producer = producer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(UserDto user) {
        producer.sendUser(new User(user.name(), user.age()));
        return Response.ok().entity("Done").build();
    }
}
