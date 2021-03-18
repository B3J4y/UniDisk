package de.unidisk.entities;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.view.results.Result;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.unidisk.entities.util.TestFactory.randomUniversityUrl;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class ProjectTest implements HibernateLifecycle {

    private CreateProjectParams createArgs(String name){
        return new CreateProjectParams("test",name);
    }
    @Test
    public void canCreateEntity() throws DuplicateException {
        final ProjectDAO dao = new ProjectDAO();
        final Project p = dao.createProject(createArgs("test"));
        Assert.assertNotNull(p);
        assertEquals(createArgs("test").getName(),p.getName());
        assertEquals(ProjectState.IDLE,p.getProjectState());
        assertEquals(0,p.getTopics().size());
    }

    @Test
    public void canSetProcessingError() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        final Project p = dao.createProject(createArgs("test"));
        Assert.assertNotNull(p);
        final String error = "test error";
        dao.setProjectError(p.getId(), error);
        final Optional<Project> optionalDbProject = dao.getProject(String.valueOf(p.getId()));
        final Project dbProject = optionalDbProject.get();
        assertEquals(error,dbProject.getProcessingError());
        dao.clearProjectError(p.getId());
        final Optional<Project> optionalDbProjectNoError = dao.getProject(String.valueOf(p.getId()));
        final Project dbProjectNoError = optionalDbProjectNoError.get();
        assertEquals("",dbProjectNoError.getProcessingError());
    }

    @Test
    public void creatingDuplicateEntityThrowsDuplicateException() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject(createArgs("test"));
        try {
            Project duplicate = dao.createProject(createArgs("test"));
            fail();
        }catch(Exception e){
            Assert.assertTrue(e instanceof DuplicateException);
        }
    }

    @Test
    public void canDeleteEntity() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject(createArgs("test"));
        dao.deleteProjectById(String.valueOf(valid.getId()));
        Optional<Project> dbProject = dao.getProject(String.valueOf(valid.getId()));
        Assert.assertNotNull(dbProject);
        Assert.assertFalse(dbProject.isPresent());
    }

    @Test
    public void canDeleteEntityWithTopics() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject(createArgs("test"));
        TopicDAO tDao = new TopicDAO();
        tDao.createTopic("test",valid.getId());
        dao.deleteProjectById(String.valueOf(valid.getId()));
        Optional<Project> dbProject = dao.getProject(String.valueOf(valid.getId()));


        Assert.assertNotNull(dbProject);
        Assert.assertFalse(dbProject.isPresent());
    }
    @Test
    public void getProjectDetailsWithoutChildren() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject(createArgs("test"));
        final Optional<Project> result = dao.getProjectDetails(String.valueOf(valid.getId()));
        Assert.assertTrue(result.isPresent());
    }

    @Test
    public void getProjectDetails() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        TopicDAO tDao = new TopicDAO();
        KeywordDAO keywordDAO = new KeywordDAO();
        Project valid = dao.createProject(createArgs("test"));

        final Topic t1 = tDao.createTopic("test",valid.getId());
        final Topic t2 =  tDao.createTopic("test2",valid.getId());
        final Topic t3 =  tDao.createTopic("test3",valid.getId());
        keywordDAO.addKeyword("k1", t1.getId());
        keywordDAO.addKeyword("k2",t1.getId());
        keywordDAO.addKeyword("k3",t2.getId());


        final Project result = dao.getProjectDetails(String.valueOf(valid.getId())).get();
        Assert.assertEquals(3,result.getTopics().size());

        final Optional<Topic> t1Result = result.getTopics().stream().filter(topic -> topic.getId() == t1.getId()).findFirst();
        Assert.assertTrue(t1Result.isPresent());
        Assert.assertEquals(2, t1Result.get().getKeywords().size());

        final Optional<Topic> t2Result = result.getTopics().stream().filter(topic -> topic.getId() == t2.getId()).findFirst();
        Assert.assertTrue(t2Result.isPresent());
        Assert.assertEquals( 1,t2Result.get().getKeywords().size());

        Assert.assertTrue(result.getTopics().stream().anyMatch(topic -> topic.getId() == t3.getId()));
    }

    @Test
    public void deletingEntityDeletesChildren() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject(createArgs("test"));
        new TopicDAO().createTopic("test",valid.getId());
        dao.deleteProjectById(String.valueOf(valid.getId()));
        Assert.assertEquals(new TopicDAO().getAll().size(),0);
    }

    @Test
    public void findEntityReturnsData() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        Project valid = dao.createProject(createArgs("test"));
        Optional<Project> projectResult = dao.getProject(String.valueOf(valid.getId()));
        Assert.assertTrue(projectResult.isPresent());
        Project dbProject = projectResult.get();
        Assert.assertEquals(valid.getName(),dbProject.getName());
        Assert.assertEquals(valid.getProjectState(),dbProject.getProjectState());
    }

    @Test
    public void findEntityReturnsNullIfMissing() {
        ProjectDAO dao = new ProjectDAO();
        Optional<Project> dbProject = dao.getProject("5555555");
        Assert.assertFalse(dbProject.isPresent());
    }

    @Test
    public void createSubproject() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        final Project parentProject = dao.createProject(createArgs("test"));
        final ProjectSubtype projectSubtype = ProjectSubtype.ByTopics;
        final Project childProject = dao.createProject(new CreateProjectParams(parentProject.getId(), projectSubtype));
        assertEquals(childProject.getParentProjectId(), parentProject.getId());
        assertEquals(childProject.getProjectSubtype(),projectSubtype);
        assertEquals(childProject.getUserId(),parentProject.getUserId());
    }

    @Test
    public void getResultsReturnsValidData() throws DuplicateException {
        final ApplicationState state = MockData.getMockState();
        final UniversityDAO uniDao = new UniversityDAO();
        final ProjectDAO projectDAO = new ProjectDAO();
        final TopicDAO topicDAO = new TopicDAO();
        final TopicScoreDAO topicScoreDAO = new TopicScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();

        final HashMap<String,Integer> generatedKeywordScores = new HashMap<String,Integer>();

        state.getUniversities().forEach((u) -> {
            final University dbUni = uniDao.addUniversity(u);
            u.setId(dbUni.getId());
        });
        final List<String> universityNames = state.getUniversities().stream().map(University::getName).collect(Collectors.toList());


        Project p = MockData.getMockState().getProjectList().get(0);

        final Project dbProject = projectDAO.createProject(createArgs(p.getName()));
        p.setId(dbProject.getId());
        projectDAO.updateProjectState(dbProject.getId(),p.getProjectState());

        state.getUniversities().forEach((university -> {
            p.getTopics().forEach((topic) -> {
                final Topic dbTopic = topicDAO.createTopic(topic.getName(),dbProject.getId()
                        ,topic.getKeywords().stream().map(Keyword::getName)
                                .collect(Collectors.toList()));
                try {
                    SearchMetaData metaData = searchMetaDataDAO.createMetaData(new URL(university.getSeedUrl()), university.getId(),
                            ZonedDateTime.now().toEpochSecond());

                    SearchMetaData metaData2 = searchMetaDataDAO.createMetaData(new URL(university.getSeedUrl()), university.getId(),
                            ZonedDateTime.now().toEpochSecond());

                    topicScoreDAO.addScore(dbTopic,1,metaData);
                    topicScoreDAO.addScore(dbTopic,.3,metaData2);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                generatedKeywordScores.put(dbTopic.getName(),dbTopic.getKeywords().size());
                dbTopic.getKeywords().forEach(keyword -> {
                    assert(keyword.getId() != 0);

                    final int scores = new Random().nextInt(10) + 2;

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
        }));

        final List<Result> results = projectDAO.getResults(String.valueOf(dbProject.getId()));
        Assert.assertEquals( p.getTopics().size() * state.getUniversities().size(),results.size());

        state.getUniversities().forEach(u -> assertTrue(results.stream().anyMatch(r -> u.getId() == r.getUniversity().getId())));

        results.forEach((topicResult) -> {
            final int expectedTopicHits = generatedKeywordScores.get(topicResult.getTopic());
            Assert.assertEquals(topicResult.getEntryCount(),expectedTopicHits);
        });
    }
}
