package de.unidisk.entities;

import de.unidisk.dao.SearchMetaDataDAO;
import de.unidisk.dao.UniversityDAO;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SearchMetaDataTest implements HibernateLifecycle{
    @Test
    void testDifferentURL() throws MalformedURLException {
        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        UniversityDAO uDAO = new UniversityDAO();
        University up = uDAO.addUniversity("Uni Potsdam");
        long now = ZonedDateTime.now().toEpochSecond();
        SearchMetaData homeUP = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), up, now);
        SearchMetaData aboutUP = smdDAO.createMetaData(new URL("http://www.uni-potsdam.de/about"), up, now);
        assertNotEquals(homeUP.getId(), aboutUP.getId(), "Different SMD's should have different ID'S");
    }

    @Test
    void testDifferentTime() throws MalformedURLException {
        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        UniversityDAO uDAO = new UniversityDAO();
        University up = uDAO.addUniversity("Uni Potsdam");
        URL home = new URL("http://www.uni-potsdam.de/home");
        SearchMetaData oldUP = smdDAO.createMetaData(home, up, LocalDateTime.now().toEpochSecond(ZoneOffset.MIN));
        SearchMetaData nowUP = smdDAO.createMetaData(home, up, ZonedDateTime.now().toEpochSecond());
        assertNotEquals(oldUP.getId(), nowUP.getId(), "Different SMD's should have different ID'S");
    }

    @Test
    void testDifferentUni() throws MalformedURLException {
        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
        UniversityDAO uDAO = new UniversityDAO();
        University up = uDAO.addUniversity("Uni Potsdam");
        University fh = uDAO.addUniversity("FH Potsdam");
        long now = ZonedDateTime.now().toEpochSecond();
        URL home = new URL("http://www.uni-potsdam.de/home");
        SearchMetaData oldUP = smdDAO.createMetaData(home, up, now);
        SearchMetaData nowUP = smdDAO.createMetaData(home, fh, now);
        assertNotEquals(oldUP.getId(), nowUP.getId(), "Different SMD's should have different ID'S");
    }
}
