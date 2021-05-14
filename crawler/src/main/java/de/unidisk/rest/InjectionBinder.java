package de.unidisk.rest;


import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.contracts.services.IAuthenticationService;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.dao.UniversityDAO;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateProjectRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.rest.authentication.AuthenticationService;
import de.unidisk.services.HibernateResultService;
import de.unidisk.services.KeywordRecommendationService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.ws.rs.ext.Provider;

@Provider
public class InjectionBinder extends AbstractBinder {


    @Override
    protected void configure() {
        bind(HibernateProjectRepo.class).to(IProjectRepository.class);
        bind(HibernateTopicRepo.class).to(ITopicRepository.class);
        bind(HibernateKeywordRepo.class).to(IKeywordRepository.class);
        bind(UniversityDAO.class).to(IUniversityRepository.class);
        bind(AuthenticationService.class).to(IAuthenticationService.class);
        bind(KeywordRecommendationService.class).to(IKeywordRecommendationService.class);
        bind(HibernateResultService.class).to(IResultService.class);
    }


}

