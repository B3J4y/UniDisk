package de.unidisk.solr;

import de.unidisk.common.ApplicationState;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.datatype.SolrStichwort;
import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.crawler.model.CrawlDocument;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.crawler.simple.SimpleSolarSystem;
import de.unidisk.dao.HibernateTestSetup;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.*;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.services.HibernateResultService;
import de.unidisk.services.KeywordRecommendationService;
import de.unidisk.services.ProjectGenerationService;
import de.unidisk.solr.nlp.datatype.RegExpStichwort;
import de.unidisk.solr.services.SolrScoringService;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.DisabledIf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SolrTest {

    @BeforeAll
    public void setup(){

    }

    @Test
    public void canLoadConfig(){
        final SolrConfiguration config = SolrConfiguration.getInstance();
       assertTrue(config.getCore().equals("unidisc"));
    }

    @Test
    @DisabledIf("System.getenv(\"CI\") == '1'")
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
    @DisabledIf("System.getenv(\"CI\") == '1'")
    public void canParseCrawlDocument() throws IOException, SolrServerException {
        final CrawlDocument document = new CrawlDocument(
                String.valueOf(System.currentTimeMillis()),
                "https://www.google.com",
                "test",
       "inhalt",
                3,
                System.currentTimeMillis(),
        1
        );
        final SolrConfiguration solrConfiguration = SolrConfiguration.getInstance();
        final SimpleSolarSystem solarSystem = new SimpleSolarSystem(solrConfiguration.getUrl());
        solarSystem.sendPageToTheMoon(document);
        final SolrConnector solrConnector = new SolrConnector(solrConfiguration);
        final List<org.apache.solr.common.SolrDocument> docs = getKeyDocs(solrConnector, "inhalt");
        assertTrue(docs.size() > 0);

        final List<ScoreResult> results = docs.stream().map(d -> {
            final CrawlDocument crawlDocument = new CrawlDocument(d);
            final int universityId = crawlDocument.universityId;
            return new ScoreResult(
                    0,
                    (float) d.get("score"),
                    universityId,
                    crawlDocument.datum,
                    crawlDocument.url
            );
        }).collect(Collectors.toList());
        assertTrue(true);
    }

    private List<org.apache.solr.common.SolrDocument> getKeyDocs(SolrConnector connector, String key) throws IOException, SolrServerException {
        QueryResponse response = connector.query(new SolrStichwort(key).buildQuery(new ArrayList<>()));
        SolrDocumentList solrList = response.getResults();

        int sizeOfStichwortResult = Math.min((int) solrList.getNumFound(), SolrConfiguration.getInstance().getRowLimit());
        ArrayList<org.apache.solr.common.SolrDocument> documents = new ArrayList<>();
        for (int i = 0; i < sizeOfStichwortResult; i++) {
            org.apache.solr.common.SolrDocument doc = solrList.get(i);

            documents.add(doc);
        }
        return documents;
    }
    @Test
    public void solrAppTest(){
        final IKeywordRepository keywordRepository = new HibernateKeywordRepo();
        final ITopicRepository topicRepository = new HibernateTopicRepo();

        final IScoringService scoringService = new SolrScoringService(keywordRepository,topicRepository, SolrConfiguration.getInstance()); final IProjectRepository projectRepository = new ProjectDAO();
        final IResultService resultService = new HibernateResultService();
        final ProjectGenerationService projectGenerationService = new ProjectGenerationService(
                projectRepository,
                topicRepository,
                keywordRepository,
                new KeywordRecommendationService()
        );

        final ApplicationState state = new ApplicationState(
                Arrays.asList(new Project("test", ProjectState.IDLE, Arrays.asList(
                        new Topic("Test", 0, Arrays.asList(new Keyword("test",0)))
                ))), Arrays.asList(new University("potsdam",0,0,"https://www.uni-potsdam.de/de/"))
        );
        HibernateTestSetup.Setup(state);


        SolrApp sapp = new SolrApp(projectRepository,scoringService,resultService, projectGenerationService);
        try {
            sapp.execute();
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
