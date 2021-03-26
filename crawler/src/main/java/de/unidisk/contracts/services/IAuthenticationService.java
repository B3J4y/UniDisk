package de.unidisk.contracts.services;

import de.unidisk.rest.authentication.ContextUser;

public interface IAuthenticationService {

    ContextUser verifyToken(String jwt);
}
