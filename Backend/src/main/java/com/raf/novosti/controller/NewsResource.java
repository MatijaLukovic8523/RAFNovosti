package com.raf.novosti.controller;

import com.raf.novosti.dao.*;
import com.raf.novosti.dto.CategoryDTO;
import com.raf.novosti.dto.CommentDTO;
import com.raf.novosti.dto.NewsDTO;
import com.raf.novosti.dto.TagDTO;
import com.raf.novosti.mapper.EntityMapper;
import com.raf.novosti.model.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/news")
public class NewsResource {

    @Inject
    private NewsDAO newsDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private CommentDAO commentDAO;

    @Inject
    private ReactionDAO reactionDAO;

    @Inject
    private CategoryDAO categoryDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("sortBy") String sortBy) {

        List<News> newsList = (sortBy != null && !sortBy.isEmpty())
                ? newsDAO.findAllSorted(sortBy, page, limit)
                : newsDAO.findAll(page, limit);

        List<NewsDTO> newsDTOs = newsList.stream().map(n -> {
            NewsDTO dto = EntityMapper.toNewsDTO(n);
            return dto;
        }).collect(Collectors.toList());

        return Response.ok(newsDTOs).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") long id){

        News news = newsDAO.findByIdWithTags(id);
        if (news == null) return Response.status(Response.Status.NOT_FOUND).build();

        List<Comment> comments = commentDAO.findByNewsId(id);
        news.setComments(comments);

        NewsDTO dto = EntityMapper.toNewsDTO(news);

        dto.setLikes(reactionDAO.countReactions(news.getId(), "LIKE", "NEWS"));
        dto.setDislikes(reactionDAO.countReactions(news.getId(), "DISLIKE", "NEWS"));

        if (dto.getComments() != null) {
            for (CommentDTO commentDto : dto.getComments()) {
                commentDto.setLikes(reactionDAO.countReactions(commentDto.getId(), "LIKE", "COMMENT"));
                commentDto.setDislikes(reactionDAO.countReactions(commentDto.getId(), "DISLIKE", "COMMENT"));
            }
        }

        return Response.ok(dto).build();
    }

    @GET
    @Path("/category/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCategory(
            @PathParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit) {

        List<News> newsList = newsDAO.findByCategoryId(categoryId, page, limit);

        List<NewsDTO> newsDTOs = newsList.stream().map(n -> {
            NewsDTO dto = new NewsDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setText(n.getText());
            dto.setPublishedAt(n.getPublishedAt());
            dto.setVisits(n.getVisits());

            if (n.getAuthor() != null) {
                dto.setAuthorName(n.getAuthor().getFirstName() + " " + n.getAuthor().getLastName());
            }
            if (n.getCategory() != null) {
                CategoryDTO catDto = new CategoryDTO();
                catDto.setId(n.getCategory().getId());
                catDto.setName(n.getCategory().getName());
                dto.setCategory(catDto);
            }
            return dto;
        }).collect(Collectors.toList());

        return Response.ok(newsDTOs).build();
    }

    @GET
    @Path("/tag/{tagId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByTag(@PathParam("tagId") Long tagId,
                             @QueryParam("page") @DefaultValue("1") int page,
                             @QueryParam("limit") @DefaultValue("10") int limit) {

        List<News> newsList = newsDAO.findByTagId(tagId, page, limit);
        List<NewsDTO> dtos = newsList.stream().map(n -> {
            NewsDTO dto = new NewsDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setText(n.getText());
            dto.setPublishedAt(n.getPublishedAt());
            dto.setVisits(n.getVisits());

            if (n.getAuthor() != null) {
                dto.setAuthorName(n.getAuthor().getFirstName() + " " + n.getAuthor().getLastName());
            }
            if (n.getCategory() != null) {
                CategoryDTO catDto = new CategoryDTO();
                catDto.setId(n.getCategory().getId());
                catDto.setName(n.getCategory().getName());
                dto.setCategory(catDto);
            }
            return dto;
        }).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/my-news")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyNews(@Context SecurityContext sc) {
        String email = sc.getUserPrincipal().getName();
        User author = userDAO.findByEmail(email);

        if (author == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<News> myNewsList = newsDAO.findByAuthorId(author.getId());

        List<NewsDTO> dtos = myNewsList.stream().map(news -> {
            NewsDTO dto = EntityMapper.toNewsDTO(news);
            return dto;
        }).collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(NewsDTO newsDto, @Context SecurityContext sc) {
        String email = sc.getUserPrincipal().getName();
        User author = userDAO.findByEmail(email);
        if (author == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        News news = new News();
        news.setTitle(newsDto.getTitle());
        news.setText(newsDto.getText());
        news.setAuthor(author);
        news.setPublishedAt(LocalDateTime.now());
        news.setVisits(0);

        if (newsDto.getCategory() == null || newsDto.getCategory().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Kategorija je obavezna!")
                    .build();
        }

        Category category = categoryDAO.findById(newsDto.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Kategorija sa tim ID-jem ne postoji"));

        news.setCategory(category);

        newsDAO.save(news);

        NewsDTO dto = EntityMapper.toNewsDTO(news);

        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext sc) {
        News news = newsDAO.findById(id).orElse(null);
        if (news == null) return Response.status(Response.Status.NOT_FOUND).build();

        String email = sc.getUserPrincipal().getName();
        User currentUser = userDAO.findByEmail(email);

        boolean isAdmin = sc.isUserInRole("ADMIN");
        boolean isAuthor = news.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isAuthor) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        newsDAO.delete(news);

        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/comments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(@PathParam("id") Long id, Comment comment) {
        News news = newsDAO.findById(id).orElse(null);
        if (news == null) return Response.status(Response.Status.NOT_FOUND).build();
        comment.setNews(news);
        comment.setCreatedAt(LocalDateTime.now());
        newsDAO.update(news);

        CommentDTO dto = EntityMapper.toCommentDTO(comment);
        dto.setLikes(0);
        dto.setDislikes(0);
        return Response.ok(dto).build();
    }

    @POST
    @Path("/reactions/comment/{commentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reactToComment(@PathParam("commentId") Long commentId,
                                   java.util.Map<String, String> body,
                                   @Context jakarta.servlet.http.HttpServletRequest request) {
        String type = body.get("type"); // "LIKE" ili "DISLIKE"
        String sessionId = request.getSession().getId();

        Reaction existing = reactionDAO.findBySessionAndTarget(sessionId, commentId, "COMMENT");
        if (existing != null) {
            existing.setReactionType(type);
            reactionDAO.update(existing);
        } else {
            reactionDAO.save(new com.raf.novosti.model.Reaction(null, commentId, "COMMENT", type, sessionId));
        }
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/visit")
    public Response recordVisit(@PathParam("id") Long id, @Context jakarta.servlet.http.HttpServletRequest request) {
        String sessionId = request.getHeader("X-Visitor-ID");

        if (sessionId == null || sessionId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!newsDAO.hasVisited(sessionId, id)) {
            newsDAO.recordVisit(sessionId, id);
        }

        return Response.ok().build();
    }

    @GET
    @Path("/popular")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPopular() {
        List<News> newsList = newsDAO.findTop3MostInteracted();

        List<NewsDTO> dtos = newsList.stream().map(n -> {
            NewsDTO dto = new NewsDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setText(n.getText());
            dto.setPublishedAt(n.getPublishedAt());
            dto.setVisits(n.getVisits());

            if (n.getAuthor() != null) {
                dto.setAuthorName(n.getAuthor().getFirstName() + " " + n.getAuthor().getLastName());
            }
            if (n.getCategory() != null) {
                CategoryDTO catDto = new CategoryDTO();
                catDto.setId(n.getCategory().getId());
                catDto.setName(n.getCategory().getName());
                dto.setCategory(catDto);
            }

            dto.setLikes(reactionDAO.countReactions(n.getId(), "LIKE", "NEWS"));
            dto.setDislikes(reactionDAO.countReactions(n.getId(), "DISLIKE", "NEWS"));

            return dto;
        }).collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateNews(NewsDTO newsDTO, @PathParam("id") Long id, @Context SecurityContext sc) {

        News news = newsDAO.findById(id).orElse(null);
        if (news == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String currentUserEmail = sc.getUserPrincipal().getName();
        if (!news.getAuthor().getEmail().equals(currentUserEmail)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Možete menjati samo svoje vesti").build();
        }

        news.setTitle(newsDTO.getTitle());
        news.setText(newsDTO.getText());

        newsDAO.update(news);

        return Response.ok(EntityMapper.toNewsDTO(news)).build();
    }
}