package com.raf.novosti.controller;

import com.raf.novosti.dao.ReactionDAO;
import com.raf.novosti.model.Reaction;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/reactions")
public class ReactionResource {

    @Inject
    private ReactionDAO reactionDAO;

    @POST
    @Path("/{type}/{targetId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response react(@PathParam("type") String type,
                          @PathParam("targetId") Long targetId,
                          Map<String, String> body,
                          @Context HttpServletRequest request) {

        String reactionType = body.get("type");
        String sessionId = request.getHeader("X-Visitor-ID");
        String targetType = type.equalsIgnoreCase("news") ? "NEWS" : "COMMENT";

        Reaction existing = reactionDAO.findBySessionAndTarget(sessionId, targetId, targetType);

        if (existing != null) {
            existing.setReactionType(reactionType);
            reactionDAO.update(existing);
        } else {
            reactionDAO.save(new Reaction(null, targetId, targetType, reactionType, sessionId));
        }
        return Response.ok().build();
    }
}
