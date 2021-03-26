package de.unidisk.rest.authentication;

import de.unidisk.contracts.services.IAuthenticationService;

public class AuthenticationService implements IAuthenticationService {

    @Override
    public ContextUser verifyToken(String jwt) {
        return new ContextUser("0","user@uni-potsdam.de");
    }
}
