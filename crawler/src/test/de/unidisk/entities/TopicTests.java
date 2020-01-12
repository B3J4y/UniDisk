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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopicTests implements HibernateLifecycle, CRUDTest, ParentTests, ChildTests {

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
    @Override
    public void canCreateEntity() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        Assert.assertNotNull(dao.createTopic(t.getName(),parentProject.getId()));
    }

    @Override
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
        final Topic dbTopic = dao.createTopic(t.getName(),t.getProjectId(),t.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()));


        Assert.assertTrue(dbTopic.getKeywords().size() == 1);
        for (Keyword keyword : dbTopic.getKeywords()) {
            Assert.assertNotEquals(keyword.getId(),0);
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

    @Override
    public void canUpdateEntity() {

    }

    @Test
    @Override
    public void canDeleteEntity() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        final Topic dbTopic = dao.createTopic(t.getName(),parentProject.getId());
        dao.deleteTopic(dbTopic.getId());
        Assert.assertEquals(dao.getAll().size(),0);
    }

    @Test
    @Override
    public void deletingEntityDeletesChildren() {
        TopicDAO dao = new TopicDAO();
        Topic valid = dao.createTopic("test", parentProject.getId());

        new KeywordDAO().addKeyword("test",valid.getId());
        dao.deleteTopic(valid.getId());
        Assert.assertEquals(new KeywordDAO().getProjectKeywords(parentProject.getId()).size(),0);
    }

    @Test
    @Override
    public void findEntityReturnsData() {
        final Topic t = DataFactory.createTopic(0);
        final TopicDAO dao = new TopicDAO();
        final Topic dbTopic = dao.createTopic(t.getName(),parentProject.getId());
        final List<Topic> topics = dao.getAll();
        Assert.assertEquals(topics.size(),1);
        Assert.assertEquals(topics.get(0).getName(), t.getName());
    }

    @Override
    public void findEntityReturnsNullIfMissing() {

    }

    @Test
    @Override
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
}
