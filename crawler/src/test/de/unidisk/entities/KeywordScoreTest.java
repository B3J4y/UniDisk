package de.unidisk.entities;

import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.entities.util.TestFactory;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordScoreTest implements HibernateLifecycle{

    @Test
    void canCreateKeywordScore(){
        final Keyword k = TestFactory.createKeyword();
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        dao.createKeywordScore(k.getId(),5);
    }

    @Test
    void createFailsIfSearchDataMissing(){
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        try{
            dao.createKeywordScore(0,5);
        }catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
            return;
        }
        fail("Missing keyword should throw an exception.");
    }

    @Test
    void createFailsIfKeywordMessing(){

    }

    @Test
    void canSetSearchMetaData() throws MalformedURLException {
        final Keyword k = TestFactory.createKeyword();
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        KeyWordScore score = dao.createKeywordScore(k.getId(),5);

        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        UniversityDAO uDAO = new UniversityDAO();
        University up = uDAO.addUniversity("Uni Potsdam");
        long now = ZonedDateTime.now().toEpochSecond();
        SearchMetaData homeUP = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up.getId(), now);
        dao.setMetaData(score.getId(),homeUP.getId());
    }

    @Test
    void getResultsReturnsValidData(){
        final Project p = new ProjectDAO().createProject("");
        final Topic t = new TopicDAO().createTopic("", p.getId());
        final Keyword k = new KeywordDAO().addKeyword("",t.getId());
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        dao.createKeywordScore(k.getId(),5);

        final List<KeyWordScore> results = new ProjectDAO().getResults(String.valueOf(p.getId()));
        assertEquals(results.size(),1);
        assertEquals(results.get(0).getScore(), 5);
    }


}
