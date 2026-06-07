package com.raf.novosti.controller;

import com.raf.novosti.dao.TagDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
@Path("/tags")
public class TagResource {
    @Inject
    private TagDAO tagDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        // Vraća listu svih tagova za padajući meni na frontendu
        return Response.ok(tagDAO.findAll()).build();
    }

}
