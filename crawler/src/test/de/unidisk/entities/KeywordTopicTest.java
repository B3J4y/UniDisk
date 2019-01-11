package de.unidisk.entities;

import de.unidisk.dao.KeywordDAO;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordTopicTest implements HibernateLifecycle{
    @Test
    void createH2Database() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
        Statement st = conn.createStatement();
        st.execute("create table customer(id integer, name varchar(10))");
        st.execute("insert into customer values (1, 'Thomas')");
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("select name from customer");
        while (rset.next()) {
            String name = rset.getString(1);
            System.out.println(name);
        }
    }

    @Test
    void testKeywordDao() {
        KeywordDAO kDao = new KeywordDAO();
        List<Pair<String, String>> keyTop = new ArrayList<>();

        keyTop.add(Pair.of("Hallo", "Hallo Welt"));
        keyTop.add(Pair.of("Welt", "Hallo Welt"));

        List<Keyword> expKeywords = kDao.addKeywords(keyTop);

        List<Keyword> keywords = kDao.findKeyWords("Hallo Welt");
        assertAll("Something went wrong with the keywords from DAO",
                () -> assertEquals(2, keywords.size(), "Size of keywords is wrong"),
                () -> assertTrue(keywords.contains(expKeywords.get(0)), "\"Hallo\" not found"),
                () -> assertTrue(keywords.contains(expKeywords.get(1)), "\"Welt\" not found")
        );
    }

    @Test
    void testTopics() {
        KeywordDAO kDao = new KeywordDAO();
        List<Pair<String, String>> keyTop = new ArrayList<>();
        String strHallo = "Hallo";
        keyTop.add(Pair.of(strHallo, "Hallo Welt"));
        keyTop.add(Pair.of("Welt", "Hallo Welt"));
        keyTop.add(Pair.of(strHallo, "Hallo Du"));
        keyTop.add(Pair.of("Du", "Hallo Du"));


        List<Keyword> expKeywords = kDao.addKeywords(keyTop);
        List<Keyword> keywords = kDao.findKeyWords("Hallo Welt");

        assertAll("Something went wrong with the keywords from DAO",
                () -> assertEquals(2, keywords.size(), "Size of keywords is wrong"),
                () -> assertTrue(keywords.contains(expKeywords.get(0)), "\"Hallo\" not found"),
                () -> assertTrue(keywords.contains(expKeywords.get(1)), "\"Welt\" not found")
        );
        List<Keyword> keywords2 = kDao.findKeyWords("Hallo Du");
        assertAll("Something went wrong with the keywords from DAO",
                () -> assertEquals(2, keywords2.size(), "Size of keywords is wrong"),
                () -> assertTrue(keywords2.contains(expKeywords.get(0)), "\"Hallo\" not found"),
                () -> assertTrue(keywords2.contains(expKeywords.get(2)), "\"Du\" not found")
        );
    }
}