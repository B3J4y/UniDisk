package de.unidisk.solr;

import de.unidisk.common.ApplicationState;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.crawler.datatype.Variable;
import de.unidisk.dao.HibernateTestSetup;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.*;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.services.HibernateResultService;
import de.unidisk.solr.nlp.datatype.RegExpStichwort;
import de.unidisk.solr.services.SolrScoringService;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class SolrTest {

    @BeforeAll
    public void setup(){

    }

    @Test
    public void canLoadConfig(){
        final SolrConfiguration config = SolrConfiguration.getInstance();
       assertTrue(config.getCore().equals("unidisk"));
    }

    @Test
    public void smokeTest() {
        SolrConnector connector = new SolrConnector(SolrConfiguration.getInstance());
        try {
            Stichwort stichwort = new RegExpStichwort("Test");
            QueryResponse response = connector.query(stichwort.buildQuery(new ArrayList<>()));
            assertTrue(response.getResults().getNumFound() >= 0);
        } catch (Exception e) {
            throw new Error(e);
        }
    }


    @Test
    public void solrAppTest(){
        final IScoringService scoringService = new SolrScoringService(new HibernateKeywordRepo(), new HibernateTopicRepo(), SolrConfiguration.getInstance());
        final IProjectRepository projectRepository = new ProjectDAO();
        final IResultService resultService = new HibernateResultService();
        final ApplicationState state = new ApplicationState(
                Arrays.asList(new Project("test", ProjectState.IDLE, Arrays.asList(
                        new Topic("Test", 0, Arrays.asList(new Keyword("test",0)))
                ))), Arrays.asList(new University("potsdam",0,0,"https://www.uni-potsdam.de/de/"))
        );
        HibernateTestSetup.Setup(state);


        SolrApp sapp = new SolrApp(projectRepository,scoringService,resultService);
        try {
            sapp.execute();
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
