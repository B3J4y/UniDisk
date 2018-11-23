package de.unidisk.entities;

import de.unidisk.HibernateUtil;
import de.unidisk.dao.KeywordDAO;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordTopicTest {
    @AfterEach
    public void afterEach() {
        HibernateUtil.truncateTable(Keyword.class);
        HibernateUtil.truncateTable(Topic.class);
    }

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
    void testKeyword() {
        Session session = HibernateUtil.getSesstionFactory().openSession();

        Transaction transaction = session.beginTransaction();
        Topic topic = new Topic();
        topic.setName("Test Keywords");
        Keyword kw = new Keyword();
        kw.setName("Hallo");
        kw.getTopics().add(topic);
        session.save(kw);
        session.save(topic);
        Keyword kworld = new Keyword();
        kworld.setName("World");
        session.save(kworld);
        Keyword keyword = session.find(Keyword.class, 1);
        transaction.commit();
        assertAll("Test keyword",
                () -> assertEquals("Hallo", keyword.getName(), "Name of keyword is wrong"),
                () -> assertEquals(1, keyword.getTopics().size(), "Size of topics is wrong"),
                () -> assertEquals("Test Keywords", keyword.getTopics()
                        .get(0)
                        .getName(), "name of first topic is wrong"));
        session.close();

        //load a new session to check if it is persisted
        session = HibernateUtil.getSesstionFactory().openSession();
        session.getTransaction().begin();
        Keyword keyword2 = session.find(Keyword.class, 1);
        session.getTransaction().commit();
        assertAll("Test keyword",
                () -> assertEquals("Hallo", keyword2.getName(), "Name of keyword is wrong"),
                () -> assertEquals(1, keyword2.getTopics().size(), "Size of topics is wrong"),
                () -> assertEquals("Test Keywords", keyword2.getTopics()
                        .get(0)
                        .getName(), "name of first topic is wrong"));
        session.close();
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