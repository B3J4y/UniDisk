package de.unidisk.services;

import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.crawler.model.CrawlDocument;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.crawler.simple.SimpleSolarSystem;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.solr.services.SolrScoringService;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolrScoringServiceTest {

    @Mock
    IKeywordRepository keywordRepository;

    @Mock
    ITopicRepository topicRepository;


    @Test
    public void canGetKeywordScores() throws IOException, SolrServerException {
        final String solrUrl = SolrConfiguration.getTestUrl();
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
                SolrConfiguration.Instance()
        );
        final List<ScoreResult> results = scoringService.getKeywordScore(0,keyword.getId());
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    public void canGetTopicScore() throws IOException, SolrServerException {
        final String solrUrl = SolrConfiguration.getTestUrl();
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
                SolrConfiguration.Instance()
        );
        final ScoreResult result = scoringService.getTopicScore(0,optionalTopic.get().getId());
        assertNotNull(result);

    }

}
