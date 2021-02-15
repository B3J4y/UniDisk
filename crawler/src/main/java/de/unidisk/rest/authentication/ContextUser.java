package de.unidisk.rest.authentication;

import javax.security.auth.Subject;
import java.security.Principal;

public class ContextUser implements Principal {

    private String id;
    private String email;

    public ContextUser(String id, String email) {
        this.id = id;
        this.email = email;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
