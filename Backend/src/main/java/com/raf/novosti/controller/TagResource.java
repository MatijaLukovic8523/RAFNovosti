package com.raf.novosti.controller;

import com.raf.novosti.dao.TagDAO;
import com.raf.novosti.dto.TagDTO;
import com.raf.novosti.model.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/tags")
public class TagResource {
    @Inject
    private TagDAO tagDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Tag> tags = new ArrayList<>();
        tags = tagDAO.findAll();

        List<TagDTO> dtos = tags.stream().map(tag -> {
            TagDTO dto = new TagDTO();
            dto.setId(tag.getId());
            dto.setName(tag.getName());

            return dto;
            }
        ).collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

}
