package com.raf.novosti.controller;

import com.raf.novosti.dao.UserDAO;
import com.raf.novosti.model.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/users")
public class UserResource {

    @Inject
    private UserDAO userDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context SecurityContext sc) {
        if (!sc.isUserInRole("ADMIN")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Samo admin može da upravlja korisnicima").build();
        }
        return Response.ok(userDAO.findAll()).build();
    }

    @PUT
    @Path("/{id}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleStatus(@PathParam("id") Long id, @Context SecurityContext sc) {
        if (!sc.isUserInRole("ADMIN")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        User user = userDAO.findById(id).orElse(null);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

        user.setActive(!user.isActive());
        userDAO.update(user);

        return Response.ok(user).build();
    }
}