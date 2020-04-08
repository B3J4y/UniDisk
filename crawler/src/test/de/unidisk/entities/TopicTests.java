package de.unidisk.entities;

import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.entities.templates.CRUDTest;
import de.unidisk.entities.templates.ChildTests;
import de.unidisk.entities.templates.ParentTests;
import de.unidisk.entities.util.TestFactory;
import de.unidisk.util.DataFactory;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopicTests implements HibernateLifecycle {

    Project parentProject;

    @Test
    void topicDeletionRemovesKeywords(){
        final Project p = TestFactory.createRawProject();
        final String name = p.getName();
        final TopicDAO topicDao = new TopicDAO();
        final Topic topic = topicDao.createTopic(name,p.getId(), Arrays.asList("test1","test2"));
        topicDao.deleteTopic(topic.getId());
        final List<Keyword> topics = new KeywordDAO().getProjectKeywords(p.getId())
                .stream().filter(t -> t.getTopicId() == topic.getId()).collect(Collectors.toList());
        assertTrue(topics.size() == 0);
    }

    @BeforeEach
    void createParentProject(){
        final ProjectDAO pDao = new ProjectDAO();
        parentProject = pDao.createProject("test");
    }

    @Test

    public void canCreateEntity() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        Assert.assertNotNull(dao.createTopic(t.getName(),parentProject.getId()));
    }


    public void creatingDuplicateEntityThrowsError() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        Assert.assertNotNull(dao.createTopic(t.getName(),parentProject.getId()));
        Assert.assertNotNull(dao.createTopic(t.getName(),parentProject.getId()));
    }

    @Test
    public void topicCreationAssignsKeywordIds(){
        final Topic t = DataFactory.createTopic(1);
        final TopicDAO dao = new TopicDAO();
        final Topic dbTopic = dao.createTopic(t.getName(),parentProject.getId(),t.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()));
        final KeywordDAO keywordDAO = new KeywordDAO();

        Assert.assertTrue(dbTopic.getKeywords().size() == 1);
        for (Keyword keyword : dbTopic.getKeywords()) {
            Assert.assertNotEquals(keyword.getId(),0);
            assertTrue(keywordDAO.keywordExists(keyword.getId()));

        }
    }

    @Test
    public void deletingTopicDeletesScores(){
        final Topic t = DataFactory.createTopic(1);

        final TopicDAO dao = new TopicDAO();
        final Topic dbTopic = dao.createTopic(t.getName(),parentProject.getId(),t.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()));
        final University uni = new UniversityDAO().addUniversity("Test");
        final SearchMetaDataDAO smDao = new SearchMetaDataDAO();
        try {
            final SearchMetaData metaData = smDao.createMetaData(new URL("https://www.google.com"),uni.getId(), new Date().getTime());
            new TopicScoreDAO().addScore(dbTopic,5,metaData);

            dao.deleteTopic(dbTopic.getId());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        };
    }


    @Test

    public void canDeleteEntity() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        final Topic dbTopic = dao.createTopic(t.getName(),parentProject.getId());
        dao.deleteTopic(dbTopic.getId());
        Assert.assertEquals(dao.getAll().size(),0);
    }

    @Test
    public void deletingEntityDeletesChildren() {
        TopicDAO dao = new TopicDAO();
        Topic valid = dao.createTopic("test", parentProject.getId());

        new KeywordDAO().addKeyword("test",valid.getId());
        dao.deleteTopic(valid.getId());
        Assert.assertEquals(new KeywordDAO().getProjectKeywords(parentProject.getId()).size(),0);
    }

    @Test
    public void findEntityReturnsData() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        final Topic dbTopic = dao.createTopic(t.getName(),parentProject.getId());
        final List<Topic> topics = dao.getAll();
        Assert.assertEquals(topics.size(),1);
        Assert.assertEquals(topics.get(0).getName(), t.getName());
    }


    @Test
    public void createEntityFailsIfParentMissing() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        try {
            final Topic dbTopic = dao.createTopic(t.getName(), -15);
        }catch(Exception e){
            Assert.assertTrue(e instanceof IllegalArgumentException);
            return;
        }
        Assert.fail();
    }

    @Test
    public void canGetTopicScoresFromKeywords(){

    }

    @Test
    public void canGetTopicScore() throws MalformedURLException {
        final Topic t = DataFactory.createTopic(2);
        t.setProjectId(parentProject.getId());
        final University u = new UniversityDAO().addUniversity(new University("UP",0,1,"https://www.google.com"));
        final TopicDAO dao = new TopicDAO();
        final Topic dbTopic = dao.createTopic(t.getName(), t.getProjectId(),t.getKeywords().stream().map(k -> k.getName()).collect(Collectors.toList()));

        final Random random = new Random();
        final Map<Integer,Double> keywordScoreMap = new HashMap<Integer,Double>();
        dbTopic.getKeywords().forEach(k -> keywordScoreMap.put(k.getId(), random.nextDouble() * 10));

        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();

        final KeywordScoreDAO keywordScoreDAO = new KeywordScoreDAO();
        double expectedScore = 0;
        for(Map.Entry<Integer,Double> entry : keywordScoreMap.entrySet()){
            final Keyword keyword = dbTopic.getKeywords().stream().filter(k -> k.getId() == entry.getKey()).findFirst().get();
            final SearchMetaData searchMetaData = searchMetaDataDAO.createMetaData(new URL(u.getSeedUrl()), u.getId(), System.currentTimeMillis());
            keywordScoreDAO.addScore(keyword, entry.getValue(),searchMetaData);
            expectedScore += entry.getValue();
        }
        expectedScore = expectedScore/ keywordScoreMap.size();

        final List<TopicScore> scores = dao.getScoresFromKeywords(dbTopic.getId());
        assertEquals(1,scores.size());
        assertEquals(expectedScore, scores.get(0).getScore());
    }
}
