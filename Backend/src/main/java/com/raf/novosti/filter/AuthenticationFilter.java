package com.raf.novosti.filter;

import com.raf.novosti.dao.UserDAO;
import com.raf.novosti.model.User;
import com.raf.novosti.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.api.ServiceLocator;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ServiceLocator serviceLocator;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        if (path.contains("auth/login") || method.equalsIgnoreCase("OPTIONS")) return;

        if (method.equalsIgnoreCase("POST") && (path.matches("news/\\d+/visit") || path.contains("reactions"))) return;

        if (method.equalsIgnoreCase("GET")) {
            if (path.equals("news") ||
                    path.startsWith("news/popular") ||
                    path.startsWith("news/category/") ||
                    path.startsWith("news/tag/") ||
                    path.matches("news/\\d+") ||
                    path.startsWith("categories") ||
                    path.startsWith("tags")) return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authHeader.substring(7);
        String email = JwtUtil.extractEmail(token);
        if (email == null || !JwtUtil.validateToken(token)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        UserDAO userDAO = serviceLocator.getService(UserDAO.class);
        User user = (userDAO != null) ? userDAO.findByEmail(email) : null;

        if (user == null || !user.isActive()) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            return;
        }

        requestContext.setSecurityContext(new SecurityContext() {
            @Override public Principal getUserPrincipal() { return () -> email; }
            @Override public boolean isUserInRole(String role) { return user.getRole().equalsIgnoreCase(role); }
            @Override public boolean isSecure() { return true; }
            @Override public String getAuthenticationScheme() { return "Bearer"; }
        });
    }
}