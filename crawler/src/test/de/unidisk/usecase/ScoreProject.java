package de.unidisk.usecase;

import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.entities.HibernateLifecycle;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.solr.services.SolrScoringService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ScoreProject implements HibernateLifecycle {

    private IKeywordRepository getKeywordRepository(){
        return new HibernateKeywordRepo();
    }

    private IScoringService getScoringService(){
        return new SolrScoringService(
                getKeywordRepository(),
                null,
                SolrConfiguration.Instance()
        );
    }

    @Test
    @Disabled
    void testConnectorWithRegex() throws Exception {


    }

    @Test
    void canInsertScore(){
        final Project p = new Project();

    }
}
