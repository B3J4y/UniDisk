package de.unidisk.entities;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.entities.templates.CRUDTest;
import de.unidisk.entities.templates.ParentTests;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.unidisk.entities.util.TestFactory.randomUniversityUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ProjectTest implements HibernateLifecycle, CRUDTest, ParentTests {

    @Test
    @Override
    public void canCreateEntity() {
        ProjectDAO dao = new ProjectDAO();

        Assert.assertNotNull(dao.createProject("test"));
    }

    @Test
    @Override
    public void creatingDuplicateEntityThrowsError() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        Project duplicate = dao.createProject("test");
        Assert.assertNull(duplicate);
    }

    @Override
    public void canUpdateEntity() {

    }

    @Test
    @Override
    public void canDeleteEntity() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        dao.deleteProjectById(String.valueOf(valid.getId()));
        Optional<Project> dbProject = dao.findProjectById(valid.getId());
        Assert.assertNotNull(dbProject);
        Assert.assertFalse(dbProject.isPresent());
    }

    @Test
    @Override
    public void deletingEntityDeletesChildren() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        ArrayList<Topic> topics = new ArrayList<Topic>();
        new TopicDAO().createTopic("test",valid.getId());
        dao.deleteProjectById(String.valueOf(valid.getId()));
        Assert.assertEquals(new TopicDAO().getAll().size(),0);
    }

    @Test
    @Override
    public void findEntityReturnsData() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        Project dbProject = dao.findProjectById(valid.getId()).get();
        Assert.assertNotNull(dbProject);
        Assert.assertEquals(valid.getName(),dbProject.getName());
        Assert.assertEquals(valid.getProjectState(),dbProject.getProjectState());
    }

    @Test
    @Override
    public void findEntityReturnsNullIfMissing() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        Optional<Project> dbProject = dao.findProjectById(5555555);
        Assert.assertNotNull(dbProject);
        Assert.assertFalse(dbProject.isPresent());
    }

    @Test
    public void getResultsReturnsValidData(){
        final ApplicationState state = MockData.getMockState();
        final UniversityDAO uniDao = new UniversityDAO();
        final ProjectDAO projectDAO = new ProjectDAO();
        final TopicDAO topicDAO = new TopicDAO();
        final TopicScoreDAO topicScoreDAO = new TopicScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();

        final HashMap<Integer,Integer> generatedKeywordScores = new HashMap<Integer,Integer>();

        state.getUniversities().forEach((u) -> {
            final University dbUni = uniDao.addUniversity(u);
            u.setId(dbUni.getId());
        });
        final List<String> universityNames = state.getUniversities().stream().map(University::getName).collect(Collectors.toList());


        Project p = MockData.getMockState().getProjectList().get(0);

        final Project dbProject = projectDAO.createProject(p.getName());
        p.setId(dbProject.getId());
        projectDAO.updateProjectState(dbProject.getId(),p.getProjectState());
        p.getTopics().forEach((topic) -> {
            final Topic dbTopic = topicDAO.createTopic(topic.getName(),dbProject.getId()
                    ,topic.getKeywords().stream().map(Keyword::getName)
                            .collect(Collectors.toList()));
            try {
                SearchMetaData metaData = searchMetaDataDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), state.getUniversities().get(0).getId(),
                        ZonedDateTime.now().toEpochSecond());

                SearchMetaData metaData2 = searchMetaDataDAO.createMetaData(new URL("http://www.uni-potsdam.de/home/contact"), state.getUniversities().get(0).getId(),
                        ZonedDateTime.now().toEpochSecond());

                topicScoreDAO.addScore(dbTopic,1,metaData);
                topicScoreDAO.addScore(dbTopic,.3,metaData2);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            dbTopic.getKeywords().forEach(keyword -> {
                assert(keyword.getId() != 0);

                final int scores = new Random().nextInt(10) + 2;
                generatedKeywordScores.put(keyword.getId(),scores);
                for(int i= 0; i < scores;i++){
                    final double score = new Random().nextDouble();

                    final University uni = state.getUniversities().get(new Random().nextInt(universityNames.size()));
                    SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
                    SearchMetaData metaData = null;
                    try {
                        metaData = smdDAO.createMetaData(new URL(randomUniversityUrl(uni.getName())), uni.getId(),
                                ZonedDateTime.now().toEpochSecond());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    KeywordScoreDAO scoreDAO = new KeywordScoreDAO();
                    scoreDAO.addScore(keyword, score, metaData);
                }
            });
        });
        final List<KeyWordScore> results = projectDAO.getResults(String.valueOf(dbProject.getId()));
        Assert.assertTrue(results.size() != 0);
        generatedKeywordScores.keySet().forEach(keywordId -> {
            final int expectedScoreCount = generatedKeywordScores.get(keywordId);
            final List<KeyWordScore> scores = results.stream().filter(k -> k.getId() == keywordId).collect(Collectors.toList());
            assertEquals(scores.size() == expectedScoreCount,
                    String.format("keyword with id %s should have %s scores but only %s were found", String.valueOf(keywordId),String.valueOf(expectedScoreCount), String.valueOf(scores.size())) );
        });
    }
    /*
    @Test
    void findProjectReturnsNullIfNotExisting(){
        final ProjectDAO dao = new ProjectDAO();
        final String name = UUID.randomUUID().toString();
        Project p = dao.createProject(name);
        final TopicDAO topicDao = new TopicDAO();
        topicDao.createTopic(name,p.getId(), Arrays.asList("test1","test2"));
        final Project inserted = dao.findProject(name).get();
        assertTrue(inserted.getTopics().size() == 1);
        assertTrue(inserted.getTopics().get(0).getKeywords().size() ==2);
    }

    @Test
    void projectDeletionRemovesTopics(){
        final ProjectDAO dao = new ProjectDAO();
        final String name = UUID.randomUUID().toString();
        Project p = dao.createProject(name);
        final TopicDAO topicDao = new TopicDAO();
        topicDao.createTopic(name,p.getId(), Arrays.asList("test1","test2"));
        dao.deleteProject(name);
        final List<Topic> topics = topicDao.getAll().stream().filter(t -> t.getProjectId() == p.getId()).collect(Collectors.toList());
        assertTrue(topics.size() == 0);
    }

    @Test
    void testCreateProject() {
        ProjectDAO pDAO = new ProjectDAO();
        assertTrue(pDAO.createProject(pName) == null, "New project could not be created");
        Optional<Project> project = pDAO.findProject(pName);
        assertAll("Project is not as expected",
                () -> assertTrue(project.isPresent(), "Project doesn't exist"),
                () -> assertEquals(pName, project.orElse(new Project()).getName(), "Projectname is not as expected"));

        List<Keyword> hwTopic = createHalloWeltTopic();
        pDAO.addTopicToProject(pName, getHWTopicName());
        Optional<Project> projectWT = pDAO.findProject(pName);
        assertAll("Project with topics is not as expected",
                () -> assertTrue(projectWT.isPresent(), "Project doesn't exist"),
                () -> assertEquals(hwTopic.get(0).getTopics().get(0), projectWT.orElse(new Project()).getTopics().get(0), "Topics are not equal"));
    }

    @Test
    void testAllProjects() {
        TopicDAO kDao = new TopicDAO();
        List<Topic> keyTop = TestFactory.createTwoTopics();
        kDao.createTopics(keyTop);

        ProjectDAO pDAO = new ProjectDAO();
        assertTrue(pDAO.createProject(pName) == null, "New project could not be created");
        assertTrue(pDAO.createProject(pName + "2") == null, "New project could not be created");
        pDAO.addTopicToProject(pName, getHWTopicName());
        pDAO.addTopicToProject(pName + "2", secondTopic);
        List<Project> projects = pDAO.getAll();
        List<String> p1Topic = projects.get(0).getTopics().stream().map(Topic::getName).collect(Collectors.toList());
        List<String> p2Topic = projects.get(1).getTopics().stream().map(Topic::getName).collect(Collectors.toList());
        assertAll("The first iteration of projects is wrong",
                () -> assertEquals(2, projects.size(), "project size is wrong"),
                () -> assertEquals(1, p1Topic.size(), "p1Topic size is wrong"),
                () -> assertEquals(1, p2Topic.size(), "p2Topic size is wrong"),
                () -> assertTrue(p1Topic.contains(getHWTopicName()), "Wrong topic in p1"),
                () -> assertTrue(p2Topic.contains(secondTopic), "Wrong topic in p2"));
        assertTrue(pDAO.createProject(pName + "3") == null, "New project could not be created");
        assertEquals(3, pDAO.getAll().size(), "There have to be 3 projects");
    }

    @Test
    void testProjectResultsAreReturned(){
        ProjectDAO pDAO = new ProjectDAO();
        TopicDAO tDao = new TopicDAO();
        KeywordDAO kDao = new KeywordDAO();

        Project p = pDAO.createProject("hello world");
        List<Topic> topics = IntStream.range(0,3).mapToObj(i -> DataFactory.createTopic(3)).collect(Collectors.toList());
        p.setTopics(topics);
        for(Topic t : topics){
            tDao.createTopic(t.getName(),p.getId());
            for(Keyword k : t.getKeywords()){
                kDao.addKeyword(k.getName(),t.getId());
            }
        }
    }*/
}
