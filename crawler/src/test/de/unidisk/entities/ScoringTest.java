package de.unidisk.entities;

import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.KeywordScoreDAO;
import de.unidisk.dao.SearchMetaDataDAO;
import de.unidisk.dao.UniversityDAO;
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
        KeywordDAO kDao = new KeywordDAO();
        List<Pair<String, String>> keyTop = new ArrayList<>();

        keyTop.add(Pair.of("Hallo", "Hallo Welt"));
        keyTop.add(Pair.of("Welt", "Hallo Welt"));

        List<Keyword> expKeywords = kDao.addKeywords(keyTop);

        UniversityDAO universityDAO = new UniversityDAO();
        University up = universityDAO.addUniversity("Uni Potsdam");

        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        SearchMetaData metaData = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up, ZonedDateTime.now()
                .toEpochSecond());
        KeywordScoreDAO scoreDAO = new KeywordScoreDAO();
        KeyWordScore halloScore = scoreDAO.addScore(expKeywords.get(0), .1, metaData);
        assertAll("keyword score is wrong",
                () -> assertEquals("Hallo", halloScore.getKeyword().getName(), "Keyword is wrong"),
                () -> assertEquals(new Double(0.1), halloScore.getScore(), "Score is wrong"),
                () -> assertEquals("Uni Potsdam", halloScore.getSearchMetaData().getUniversity().getName()));

        KeyWordScore weltScore = scoreDAO.addScore(expKeywords.get(1), .2, metaData);
        assertAll("keyword score is wrong",
                () -> assertEquals("Welt", weltScore.getKeyword().getName(), "Keyword is wrong"),
                () -> assertEquals(new Double(0.2), weltScore.getScore(), "Score is wrong"),
                () -> assertEquals("Uni Potsdam", weltScore.getSearchMetaData().getUniversity().getName()));
    }
}
