package de.unidisk.entities;

import de.unidisk.dao.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringTest implements HibernateLifecycle {

    @Test
    void testKeywordScoring() throws MalformedURLException {
        UniversityDAO universityDAO = new UniversityDAO();
        University up = universityDAO.addUniversity("Uni Potsdam");
        KeywordDAO kDao = new KeywordDAO();
        List<Pair<String, String>> keyTop = new ArrayList<>();

        keyTop.add(Pair.of("Hallo", "Hallo Welt"));
        keyTop.add(Pair.of("Welt", "Hallo Welt"));

        List<Keyword> expKeywords = kDao.addKeywords(keyTop);

        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        SearchMetaData metaData = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up,
                ZonedDateTime.now().toEpochSecond());
        KeywordScoreDAO scoreDAO = new KeywordScoreDAO();
        ScoredInput halloScore = scoreDAO.addScore(expKeywords.get(0), .1, metaData);
        assertAll("keyword score is wrong",
                () -> assertEquals("Hallo", halloScore.getInputName(), "Keyword is wrong"),
                () -> assertEquals(0.1, halloScore.getScore(), "Score is wrong"),
                () -> assertEquals("Uni Potsdam", halloScore.getUniName()));

        ScoredInput weltScore = scoreDAO.addScore(expKeywords.get(1), .2, metaData);
        assertAll("keyword score is wrong",
                () -> assertEquals("Welt", weltScore.getInputName(), "Keyword is wrong"),
                () -> assertEquals(0.2, weltScore.getScore(), "Score is wrong"),
                () -> assertEquals("Uni Potsdam", weltScore.getUniName()));
    }

    @Test
    void testTopicScoring() throws MalformedURLException {
        UniversityDAO universityDAO = new UniversityDAO();
        University up = universityDAO.addUniversity("Uni Potsdam");
        createHalloWeltTopic();
        TopicDAO topicDAO = new TopicDAO();
        Topic topic = topicDAO.findOrCreateTopic("Hallo Welt").orElse(new Topic());

        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        SearchMetaData metaData = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up,
                ZonedDateTime.now().toEpochSecond());

        TopicScoreDAO scoreDAO = new TopicScoreDAO();
        ScoredInput scoredInput = scoreDAO.addScore(topic, .5, metaData);
        assertAll("Topic score is wrong",
                () -> assertEquals("Hallo Welt", scoredInput.getInputName()),
                () -> assertEquals(.5, scoredInput.getScore()),
                () -> assertEquals("Uni Potsdam", scoredInput.getUniName()));
    }
}
