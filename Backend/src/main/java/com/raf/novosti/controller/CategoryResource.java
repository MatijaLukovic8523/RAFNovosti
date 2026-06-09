package com.raf.novosti.controller;

import com.raf.novosti.dao.CategoryDAO;
import com.raf.novosti.dto.CategoryDTO;
import com.raf.novosti.model.Category;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/categories")
public class CategoryResource {

    @Inject
    private CategoryDAO categoryDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Category> categories = categoryDAO.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream().map(c -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setDescription(c.getDescription());
            return dto;
        }).collect(Collectors.toList());
        return Response.ok(categoryDTOs).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryDAO.save(category);
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, CategoryDTO dto) {
        Category category = categoryDAO.findById(id).orElseThrow(() -> new NotFoundException());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryDAO.update(category);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            Category category = categoryDAO.findById(id).orElseThrow(() -> new NotFoundException());
            categoryDAO.delete(category);
            return Response.noContent().build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}