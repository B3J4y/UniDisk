package de.unidisk.entities;

import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringTest implements HibernateLifecycle {


    @Test
    void testKeywordScoring() throws MalformedURLException {

        ProjectDAO pDao = new ProjectDAO();
        Project project = pDao.createProject("parent");

        UniversityDAO universityDAO = new UniversityDAO();
        University up = universityDAO.addUniversity("Uni Potsdam");
        TopicDAO kDao = new TopicDAO();
        List<Topic> keyTop = new ArrayList<>();

        keyTop.add(new Topic("hallo",project.getId(),Arrays.asList(new Keyword("Hallo Welt"))));
        keyTop.add(new Topic("welt",project.getId(),Arrays.asList(new Keyword("Hallo Welt"))));

        List<Topic> expKeywords = keyTop.stream().map(
                t -> kDao.createTopic(t.getName(),t.getProjectId(),t.getKeywords().stream().map(Keyword::getName)
                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        SearchMetaData metaData = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up.getId(),
                ZonedDateTime.now().toEpochSecond());
        KeywordScoreDAO scoreDAO = new KeywordScoreDAO();
        ScoredInput halloScore = scoreDAO.addScore(expKeywords.get(0).getKeywords().get(0), .1, metaData);
        assertAll("keyword score is wrong",
                () -> assertEquals(expKeywords.get(0).getKeywords().get(0).getId(), halloScore.getInput().getId(), "Keyword is wrong"),
                () -> assertEquals(0.1, halloScore.getScore(), "Score is wrong"),
                () -> assertEquals("Uni Potsdam", halloScore.getUniName()));

        ScoredInput weltScore = scoreDAO.addScore(expKeywords.get(1).getKeywords().get(0), .2, metaData);
        assertAll("keyword score is wrong",
                () -> assertEquals(expKeywords.get(1).getKeywords().get(0).getId(), weltScore.getInput().getId(), "Keyword is wrong"),
                () -> assertEquals(0.2, weltScore.getScore(), "Score is wrong"),
                () -> assertEquals("Uni Potsdam", weltScore.getUniName()));
    }

    @Test
    void testTopicScoring() throws MalformedURLException {
        UniversityDAO universityDAO = new UniversityDAO();
        University up = universityDAO.addUniversity("Uni Potsdam");

        final Project parentProject = new ProjectDAO().createProject("test");
        TopicDAO topicDAO = new TopicDAO();
        Topic topic = topicDAO.createTopic("Hallo Welt",parentProject.getId());

        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        SearchMetaData metaData = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up.getId(),
                ZonedDateTime.now().toEpochSecond());

        TopicScoreDAO scoreDAO = new TopicScoreDAO();
        ScoredInput scoredInput = scoreDAO.addScore(topic, .5, metaData);
        assertAll("Topic score is wrong",
                () -> assertEquals(topic.getId(), scoredInput.getInput().getId()),
                () -> assertEquals(.5, scoredInput.getScore()),
                () -> assertEquals("Uni Potsdam", scoredInput.getUniName()));
    }
}
