package de.unidisk.contracts.services;

import de.unidisk.crawler.rest.authentication.ContextUser;

public interface IAuthenticationService {

    ContextUser verifyToken(String jwt);
}
