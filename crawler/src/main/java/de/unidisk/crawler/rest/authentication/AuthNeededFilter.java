package de.unidisk.crawler.rest.authentication;

import de.unidisk.contracts.services.IAuthenticationService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@AuthNeeded
@Priority(Priorities.AUTHENTICATION)
public class AuthNeededFilter implements ContainerRequestFilter {


    @Inject
    IAuthenticationService authenticationService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();
        try {
            final ContextUser contextUser = authenticationService.verifyToken(token);
            final AuthenticatedContext context = new AuthenticatedContext(contextUser);
            requestContext.setSecurityContext(context);
        } catch (Exception e) {
            System.out.println("#### invalid token : " + token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}