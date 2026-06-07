package com.raf.novosti.controller;

import com.raf.novosti.dao.NewsDAO;
import com.raf.novosti.dao.UserDAO;
import com.raf.novosti.model.News;
import com.raf.novosti.model.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.time.LocalDateTime;

@Path("/news")
public class NewsResource {

    @Inject
    private NewsDAO newsDAO;

    @Inject
    private UserDAO userDAO;

    private boolean canModifyNews(User currentUser, News news) {
        return currentUser.getRole().equals("ADMIN") ||
                news.getAuthor().getId().equals(currentUser.getId());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
                           @QueryParam("limit") @DefaultValue("10") int limit) {
        // Sada možemo koristiti paginaciju koju smo dodali u AbstractDAO
        return Response.ok(newsDAO.findAll(page, limit)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(News news, @Context SecurityContext sc) {
        // Validacija obaveznih polja (Zadatak: "не смеју да буду null, нити празни стрингови")
        if (news.getTitle() == null || news.getTitle().trim().isEmpty() ||
                news.getText() == null || news.getText().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Naslov i tekst su obavezni.").build();
        }

        news.setPublishedAt(LocalDateTime.now());
        news.setVisits(0);

        // OVO JE KLJUČNO: Ko je autor?
        // Treba da postaviš autora vesti na osnovu ulogovanog korisnika
        String email = sc.getUserPrincipal().getName(); // DODAJ @Context SecurityContext
        User author = userDAO.findByEmail(email);
        news.setAuthor(author);

        newsDAO.save(news);
        return Response.ok(news).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        // 1. Pronađi vest
        News news = newsDAO.findById(id).orElse(null);
        if (news == null) return Response.status(Response.Status.NOT_FOUND).build();

        // 2. Pronađi ulogovanog korisnika (koristeći email iz tokena koji bi trebao biti u SecurityContext)
        String email = securityContext.getUserPrincipal().getName();
        User currentUser = userDAO.findByEmail(email);

        // 3. Provera autorizacije
        if (!canModifyNews(currentUser, news)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Nemate dozvolu za brisanje ove vesti").build();
        }

        newsDAO.delete(news);
        return Response.noContent().build();
    }


}