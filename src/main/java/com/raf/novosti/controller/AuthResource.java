package com.raf.novosti.controller;

import com.raf.novosti.dao.UserDAO;
import com.raf.novosti.dto.LoginDTO;
import com.raf.novosti.model.User;
import com.raf.novosti.util.PasswordHasher;
import jakarta.inject.Inject;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {
    @Inject
    private UserDAO userDAO;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO loginDTO) {
        User user = userDAO.findByEmail(loginDTO.getEmail());

        if (user == null || !user.isActive()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Neispravni kredencijali.").build();
        }

        String hashedInput = PasswordHasher.hashPassword(loginDTO.getPassword());
        if (!user.getPassword().equals(hashedInput)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Neispravni kredencijali.").build();
        }

        //TODO gen tokena
        return Response.ok("Uspešna prijava").build();
    }
}