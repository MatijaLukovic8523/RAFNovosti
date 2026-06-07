package com.raf.novosti.filter;

import com.raf.novosti.dao.UserDAO;
import com.raf.novosti.model.User;
import com.raf.novosti.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private UserDAO userDAO; // Injektujemo DAO direktno

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.contains("auth/login")) return;

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Nedostaje token").build());
            return;
        }

        String token = authHeader.substring(7);
        String email = JwtUtil.extractEmail(token); // OVO MORAŠ IMATI U JwtUtil

        if (email == null || !JwtUtil.validateToken(token)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Neispravan token").build());
            return;
        }

        // PROVERA KORISNIKA U BAZI
        User user = userDAO.findByEmail(email);
        if (user == null || !user.isActive()) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Korisnik nije aktivan ili ne postoji").build());
            return;
        }

        // POSTAVLJANJE SECURITY CONTEXT-A (Da bi sc.isUserInRole("ADMIN") radio u kontrolerima)
        requestContext.setSecurityContext(new SecurityContext() {
            @Override public Principal getUserPrincipal() { return () -> email; }
            @Override public boolean isUserInRole(String role) { return user.getRole().equalsIgnoreCase(role); }
            @Override public boolean isSecure() { return true; }
            @Override public String getAuthenticationScheme() { return "Bearer"; }
        });
    }
}