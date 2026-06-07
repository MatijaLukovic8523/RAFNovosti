package com.raf.novosti.controller;

import com.raf.novosti.dao.CategoryDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/categories")
public class CategoryResource {

    @Inject
    private CategoryDAO categoryDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(categoryDAO.findAll()).build();
    }
}