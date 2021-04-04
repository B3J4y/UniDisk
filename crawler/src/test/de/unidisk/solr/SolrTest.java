package de.unidisk.solr;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.exceptions.EntityNotFoundException;
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
import de.unidisk.entities.HibernateLifecycle;
import de.unidisk.entities.hibernate.*;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.services.HibernateResultService;
import de.unidisk.solr.nlp.datatype.RegExpStichwort;
import de.unidisk.solr.services.SolrScoringService;
import de.unidisk.util.SolrLifecycle;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class SolrTest implements HibernateLifecycle {

    IKeywordRepository keywordRepository;
    ITopicRepository topicRepository;

    final CrawlDocument document = new CrawlDocument(
            String.valueOf(System.currentTimeMillis()),
            "https://www.google.com",
            "test",
            "inhalt",
            3,
            System.currentTimeMillis(),
            1
    );


    public void setupMocks(){
        keywordRepository = Mockito.mock(IKeywordRepository.class);
        topicRepository = Mockito.mock(ITopicRepository.class);
    }

    @Test
    public void canLoadConfig(){
        final SolrConfiguration config = SolrConfiguration.getInstance();
       assertTrue(config.getCore().equals("unidisc"));
    }

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        SolrLifecycle.setup();
    }

    @AfterAll
    public static void clean() throws IOException, InterruptedException {
        SolrLifecycle.clean();
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "1")
    public void smokeTest() throws IOException, InterruptedException {
        SolrConnector connector = new SolrConnector(SolrConfiguration.getInstance());
        try {
            final SolrConfiguration solrConfiguration = SolrConfiguration.getInstance();
            final SimpleSolarSystem solarSystem = new SimpleSolarSystem(solrConfiguration.getUrl());

            solarSystem.sendPageToTheMoon(document);

            Stichwort stichwort = new RegExpStichwort(document.title);
            QueryResponse response = connector.query(stichwort.buildQuery(new ArrayList<>()));
            assertTrue(response.getResults().getNumFound() >= 0);
        } catch (Exception e) {
            throw new Error(e);
        }
    }


    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "1")
    public void canParseCrawlDocument() throws IOException, SolrServerException, InterruptedException {
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

        final IScoringService scoringService = new SolrScoringService(keywordRepository,topicRepository, SolrConfiguration.getInstance());
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


    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "1")
    public void canGetKeywordScores() throws IOException, SolrServerException {
        setupMocks();
        final String solrUrl = SolrConfiguration.getInstance().getUrl();
        final SimpleSolarSystem simpleSolarSystem = new SimpleSolarSystem(solrUrl);
        final Keyword keyword = new Keyword(
                "randomkeywordstringstuff",
                0
        );
        simpleSolarSystem.sendPageToTheMoon(new CrawlDocument("x", "https://www.google.com", "title", keyword.getName(), 0, System.currentTimeMillis(), 0));
        final Optional<Keyword> optionalKeyword = Optional.of(keyword);
        when(keywordRepository.getKeyword(keyword.getId())).thenReturn(optionalKeyword);
        final SolrScoringService scoringService = new SolrScoringService(
                keywordRepository,
                topicRepository,
                SolrConfiguration.getInstance()
        );
        final List<ScoreResult> results = scoringService.getKeywordScore(0,keyword.getId());
        assertNotNull(results);
        Assertions.assertTrue(results.size() > 0);
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "1")
    public void canGetTopicScore() throws IOException, SolrServerException, EntityNotFoundException {
        setupMocks();
        final String solrUrl = SolrConfiguration.getInstance().getUrl();
        final SimpleSolarSystem simpleSolarSystem = new SimpleSolarSystem(solrUrl);
        final Topic topic = new Topic(
                "randomtopicstringstuff",
                0
        );
        simpleSolarSystem.sendPageToTheMoon(new CrawlDocument("x", "https://www.google.com", "title", topic.getName(), 0, System.currentTimeMillis(), 0));
        final Optional<Topic> optionalTopic = Optional.of(topic);
        when(topicRepository.getTopic(topic.getId())).thenReturn(optionalTopic);
        final SolrScoringService scoringService = new SolrScoringService(
                keywordRepository,
                topicRepository,
                SolrConfiguration.getInstance()
        );
        final List<ScoreResult> result = scoringService.getTopicScores(0,optionalTopic.get().getId());
        assertNotNull(result);

    }
}
