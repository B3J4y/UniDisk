package de.unidisk.crawler.rest.authentication;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class AuthenticatedContext implements SecurityContext {

    private ContextUser contextUser;

    public AuthenticatedContext(ContextUser contextUser) {
        this.contextUser = contextUser;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.contextUser;
    }

    @Override
    public boolean isUserInRole(String s) {
        return true;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }

    public ContextUser getUser() {
        return contextUser;
    }
}
