package de.unidisk.crawler.rest;


import de.unidisk.*;

import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.services.IAuthenticationService;
import de.unidisk.crawler.rest.authentication.AuthenticationService;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateProjectRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.ws.rs.ext.Provider;

@Provider
public class InjectionBinder extends AbstractBinder {


    @Override
    protected void configure() {
        bind(HibernateProjectRepo.class).to(IProjectRepository.class);
        bind(HibernateTopicRepo.class).to(ITopicRepository.class);
        bind(HibernateKeywordRepo.class).to(IKeywordRepository.class);
        bind(AuthenticationService.class).to(IAuthenticationService.class);
    }


}

