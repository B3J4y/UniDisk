package de.unidisk.entities;

import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.entities.templates.CRUDTest;
import de.unidisk.entities.templates.ChildTests;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeywordTest implements HibernateLifecycle, CRUDTest, ChildTests {
    private String secondTopic = "Hallo Du";
    Topic parentTopic;

    @BeforeEach
    void setupTopic(){
        Project p = new ProjectDAO().createProject(new IProjectRepository.CreateProjectArgs("p","test"));
        parentTopic = new TopicDAO().createTopic("n",p.getId());
    }

    @Test
    @Override
    public void canCreateEntity() {
        KeywordDAO dao = new KeywordDAO();
        Assert.assertNotNull(dao.addKeyword("test",parentTopic.getId()));
    }

    @Test
    @Override
    public void creatingDuplicateEntityThrowsError() {
        KeywordDAO dao = new KeywordDAO();
        Assert.assertNotNull(dao.addKeyword("test",parentTopic.getId()));
        Assert.assertNull(dao.addKeyword("test",parentTopic.getId()));
    }

    @Override
    public void canUpdateEntity() {

    }

    @Test
    @Override
    public void canDeleteEntity() {
        KeywordDAO dao = new KeywordDAO();
        Keyword k = dao.addKeyword("test",parentTopic.getId());
        Assert.assertNotNull(k);
        dao.deleteKeyword(k.getId());
        Assert.assertFalse(dao.keywordExists(k.getId()));
    }

    @Override
    public void findEntityReturnsData() {

    }

    @Override
    public void findEntityReturnsNullIfMissing() {

    }

    @Test
    @Override
    public void createEntityFailsIfParentMissing() {
        KeywordDAO dao = new KeywordDAO();
        try {
            Keyword k = dao.addKeyword("test", parentTopic.getId() + 1);
        }catch(Exception e){
            Assert.assertTrue(e instanceof IllegalArgumentException);
            return;
        }
        Assert.fail();
    }

    /*
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
        }
    }

    @Test
    void testKeywordDao() {
        KeywordDAO kDao = new KeywordDAO();
        List<Keyword> expKeywords = createHalloWeltTopic();
        List<Keyword> keywords = kDao.findKeyWordsByTopic("Hallo Welt");
        assertAll("Something went wrong with the keywords from DAO",
                () -> assertEquals(2, keywords.size(), "Size of keywords is wrong"),
                () -> assertTrue(keywords.contains(expKeywords.get(0)), "\"Hallo\" not found"),
                () -> assertTrue(keywords.contains(expKeywords.get(1)), "\"Welt\" not found")
        );
    }

    @Test
    void testTopics() {
        KeywordDAO kDao = new KeywordDAO();
        TopicDAO topicDao = new TopicDAO();
        List<Topic> keyTop = TestFactory.createTwoTopics();


        List<Topic> expKeywords = topicDao.createTopics(keyTop);
        List<Keyword> keywords = kDao.findKeyWordsByTopic(getHWTopicName());

        assertAll("Something went wrong with the keywords from DAO",
                () -> assertEquals(2, keywords.size(), "Size of keywords is wrong"),
                () -> assertTrue(keywords.contains(expKeywords.get(0)), "\"Hallo\" not found"),
                () -> assertTrue(keywords.contains(expKeywords.get(1)), "\"Welt\" not found")
        );
        List<Keyword> keywords2 = kDao.findKeyWordsByTopic(secondTopic);
        assertAll("Something went wrong with the keywords from DAO",
                () -> assertEquals(2, keywords2.size(), "Size of keywords is wrong"),
                () -> assertTrue(keywords2.contains(expKeywords.get(0)), "\"Hallo\" not found"),
                () -> assertTrue(keywords2.contains(expKeywords.get(2)), "\"Du\" not found")
        );
    }

    private List<Pair<String, String>> createTwoTopics() {
        List<Pair<String, String>> keyTop = new ArrayList<>();
        String strHallo = "Hallo";
        keyTop.add(Pair.of(strHallo, getHWTopicName()));
        keyTop.add(Pair.of("Welt", getHWTopicName()));
        keyTop.add(Pair.of(strHallo, secondTopic));
        keyTop.add(Pair.of("Du", secondTopic));
        return keyTop;
    }

    @Test
    void testGetAllTopics() {
        KeywordDAO kDao = new KeywordDAO();
        List<Topic> keyTop = TestFactory.createTwoTopics();
        new TopicDAO().createTopics(keyTop);
        TopicDAO topicDAO = new TopicDAO();
        List<Topic> topics = topicDAO.getAll();
        List<String> topicNames = topics.stream().map(Topic::getName).collect(Collectors.toList());
        assertAll("Could not query all topics",
                () -> assertEquals(2, topics.size(), "Size of topics is not correct"),
                () -> assertTrue(topicNames.contains(getHWTopicName()), getHWTopicName() + " is not a topic"),
                () -> assertTrue(topicNames.contains(secondTopic), secondTopic + " is not a topic"));
    }*/
}