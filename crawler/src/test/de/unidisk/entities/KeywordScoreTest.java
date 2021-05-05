package de.unidisk.entities;

import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.KeyWordScore;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.SearchMetaData;
import de.unidisk.entities.hibernate.University;
import de.unidisk.entities.util.TestFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class KeywordScoreTest implements HibernateLifecycle{

    @Test
    void canCreateKeywordScore(){
        final Keyword k = TestFactory.createKeyword();
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        dao.createKeywordScore(k.getId(),5,"title");


    }

    @Test
    void canCreateMultipleKeywordScore(){
        final Keyword k = TestFactory.createKeyword();
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        dao.createKeywordScore(k.getId(),5,"title");
        dao.createKeywordScore(k.getId(),3,"title2");
        HibernateUtil.execute(session -> {
            final long count = session.createQuery("Select p from KeyWordScore  p").stream().count();
            Assert.assertEquals(2,count);
            return null;
        });
    }


    @Test
    void loadsKeywordScoreForKeyword(){
        final Keyword k = TestFactory.createKeyword();
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        dao.createKeywordScore(k.getId(),5,"title");
        final Keyword loadedKeyword = new KeywordDAO().get(k.getId()).get();
        assertEquals(loadedKeyword.getKeyWordScores().size(),1);
    }

    @Test
    void createFailsIfSearchDataMissing(){
        final KeywordScoreDAO dao = new KeywordScoreDAO();
        try{
            dao.createKeywordScore(0,5,"title");
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
        KeyWordScore score = dao.createKeywordScore(k.getId(),5,"title");

        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        UniversityDAO uDAO = new UniversityDAO();
        University up = uDAO.addUniversity("Uni Potsdam");
        long now = ZonedDateTime.now().toEpochSecond();
        SearchMetaData homeUP = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up.getId(), now);
        dao.setMetaData(score.getId(),homeUP.getId());
    }

}
