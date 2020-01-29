package de.unidisk.entities;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.entities.templates.CRUDTest;
import de.unidisk.entities.templates.ParentTests;
import de.unidisk.view.model.MapMarker;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.unidisk.entities.util.TestFactory.randomUniversityUrl;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ProjectTest implements HibernateLifecycle, CRUDTest, ParentTests {

    @Test
    @Override
    public void canCreateEntity() {
        ProjectDAO dao = new ProjectDAO();

        Assert.assertNotNull(dao.createProject("test"));
    }

    @Override
    public void creatingDuplicateEntityThrowsError() {}

    @Test

    public void creatingDuplicateEntityReturnsExisting() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        Project duplicate = dao.createProject("test");
        Assert.assertNotNull(duplicate);
        Assert.assertEquals(valid.getId(), duplicate.getId());
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
    public void canDeleteEntityWithTopics() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        TopicDAO tDao = new TopicDAO();
        tDao.createTopic("test",valid.getId());
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
        new TopicDAO().createTopic("test",valid.getId());
        dao.deleteProjectById(String.valueOf(valid.getId()));
        Assert.assertEquals(new TopicDAO().getAll().size(),0);
    }

    @Test
    @Override
    public void findEntityReturnsData() {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject("test");
        Optional<Project> projectResult = dao.findProjectById(valid.getId());
        Assert.assertTrue(projectResult.isPresent());
        Project dbProject = projectResult.get();
        Assert.assertEquals(valid.getName(),dbProject.getName());
        Assert.assertEquals(valid.getProjectState(),dbProject.getProjectState());
    }

    @Test
    @Override
    public void findEntityReturnsNullIfMissing() {
        ProjectDAO dao = new ProjectDAO();
        Optional<Project> dbProject = dao.findProjectById(5555555);
        Assert.assertFalse(dbProject.isPresent());
    }

    @Test
    public void getMapMarkerReturnsValidData(){

        final UniversityDAO uniDao = new UniversityDAO();
        final ProjectDAO projectDAO = new ProjectDAO();

        final TopicDAO topicDAO = new TopicDAO();
        final TopicScoreDAO topicScoreDAO = new TopicScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();

        final List<University> universities = Arrays.asList(
            new University("1",1,1),
                new University("2",2,2)

        );

        universities.forEach((u) -> {
            final University dbUni = uniDao.addUniversity(u);
            u.setId(dbUni.getId());
        });


        Project p = new Project("test", ProjectState.FINISHED, Arrays.asList(
                new Topic("5",0),
                new Topic("7",0)
        ) );

        final Project dbProject = projectDAO.createProject(p.getName());
        p.setId(dbProject.getId());
        projectDAO.updateProjectState(dbProject.getId(),p.getProjectState());

        p.getTopics().forEach((topic) -> {
            final int index = p.getTopics().indexOf(topic);
            final Topic dbTopic = topicDAO.createTopic(topic.getName(), dbProject.getId()
                    , topic.getKeywords().stream().map(Keyword::getName)
                            .collect(Collectors.toList()));
            try {
                SearchMetaData metaData = searchMetaDataDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), universities.get(index).getId(),
                        ZonedDateTime.now().toEpochSecond());


                topicScoreDAO.addScore(dbTopic, 1, metaData);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        });

        final List<MapMarker> expectedMarker = Arrays.asList(
            new MapMarker(
                    "5",0, universities.get(0)

            )  ,
            new MapMarker(
                    "7",0, universities.get(1)

                    )
        );

        final List<MapMarker> mapMarker = projectDAO.getMapMarker(String.valueOf(dbProject.getId()));
        assertEquals(mapMarker.size(),2);
        mapMarker.forEach(marker -> {
            final Optional<MapMarker> expected = expectedMarker.stream().filter(e -> marker.getTopicName().equals(e.getTopicName())).findFirst();
            assertTrue(expected.isPresent());
            final MapMarker expectedValue = expected.get();
            assertEquals(expectedValue.getLat(), marker.getLat());
            assertEquals(expectedValue.getLng(), marker.getLng());
            assertEquals(expectedValue.getUniversity().getName(),marker.getUniversity().getName());
        });
    }


    /*
    Beziehung von Keyword zu Score ist momentan 1:1 statt 1:n.
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
    }*/
}
