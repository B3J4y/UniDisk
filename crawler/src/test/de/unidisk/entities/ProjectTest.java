package de.unidisk.entities;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.rest.dto.topic.RateTopicResultDto;
import de.unidisk.view.results.Result;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.unidisk.entities.util.TestFactory.createKeyword;
import static de.unidisk.entities.util.TestFactory.randomUniversityUrl;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.*;


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
    public void getResultsReturnsValidData() throws DuplicateException, MalformedURLException {
        final ApplicationState state = MockData.getMockState();
        final UniversityDAO uniDao = new UniversityDAO();
        final ProjectDAO projectDAO = new ProjectDAO();
        final TopicDAO topicDAO = new TopicDAO();
        final TopicScoreDAO topicScoreDAO = new TopicScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();
        state.getUniversities().forEach((u) -> {
            final University dbUni = uniDao.addUniversity(u);
            u.setId(dbUni.getId());
        });
        final List<String> universityNames = state.getUniversities().stream().map(University::getName).collect(Collectors.toList());


        Project p = MockData.getMockState().getProjectList().get(0);

        final Project dbProject = projectDAO.createProject(createArgs(p.getName()));
        p.setId(dbProject.getId());
        projectDAO.updateProjectState(dbProject.getId(),p.getProjectState());

        for(University university : state.getUniversities()){
            final HashMap<Integer,Integer> universityScores = new HashMap<>();

            for(Topic topic : p.getTopics()){
                final Topic dbTopic = topicDAO.createTopic(topic.getName(),dbProject.getId()
                        ,topic.getKeywords().stream().map(Keyword::getName)
                                .collect(Collectors.toList()
                                ));

                SearchMetaData metaData = searchMetaDataDAO.createMetaData(new URL(university.getSeedUrl()), university.getId(),
                        ZonedDateTime.now().toEpochSecond());

                topicScoreDAO.addScore(dbTopic,1,metaData);


                for (Keyword keyword : dbTopic.getKeywords()) {
                    assert (keyword.getId() != 0);

                    final int scores = new Random().nextInt(10) + 2;

                    for (int i = 0; i < scores; i++) {
                        final double score = new Random().nextDouble();

                        final University uni = state.getUniversities().get(new Random().nextInt(universityNames.size()));

                        SearchMetaData keywordMetaData = searchMetaDataDAO.createMetaData(new URL(randomUniversityUrl(uni.getName())), uni.getId(),
                                ZonedDateTime.now().toEpochSecond()
                        );

                        KeywordScoreDAO scoreDAO = new KeywordScoreDAO();
                        KeyWordScore keyWordScore = scoreDAO.createKeywordScore(keyword.getId(), score, "page");
                        scoreDAO.setMetaData(keyWordScore.getId(), keywordMetaData.getId());
                    }
                }
            }
        }

        final List<Result> results = projectDAO.getResults(String.valueOf(dbProject.getId()));
        assertEquals( p.getTopics().size() * state.getUniversities().size(),results.size());
        state.getUniversities().forEach(u -> Assertions.assertTrue(results.stream().anyMatch(r -> u.getId() == r.getUniversity().getId())));
    }

    @Test
    public void createSubproject() throws DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        final Project parentProject = dao.createProject(createArgs("test"));
        final ProjectSubtype projectSubtype = ProjectSubtype.BY_TOPICS;
        final Project childProject = dao.createProject(CreateProjectParams.subproject(parentProject.getId(), projectSubtype));
        assertEquals(childProject.getParentProjectId().intValue(), parentProject.getId());
        assertEquals(childProject.getProjectSubtype(),projectSubtype);
        assertEquals(childProject.getUserId(),parentProject.getUserId());
        assertEquals(childProject.getProjectState(), ProjectState.IDLE);
    }

    @Test
    public void generateSubprojectByCustom() throws DuplicateException, EntityNotFoundException {
       final ProjectDAO dao = new ProjectDAO();
       final TopicDAO topicDao = new TopicDAO();
       final KeywordDAO keywordDAO = new KeywordDAO();
       final Project parentProject = dao.createProject(createArgs("test"));
       final Topic topic = topicDao.createTopic("test", parentProject.getId());
       final Keyword keyword = keywordDAO.createKeyword(new CreateKeywordParams("custom", String.valueOf(topic.getId()),false));
       keywordDAO.createKeyword(new CreateKeywordParams("suggested", String.valueOf(topic.getId()),true));
       final Project copy = dao.generateSubprojectByCustom(String.valueOf(parentProject.getId()));
        assertEquals(1,copy.getTopics().size());
        assertEquals(1,copy.getTopics().get(0).getKeywords().size());
        assertEquals(keyword.getName(),copy.getTopics().get(0).getKeywords().get(0).getName());
    }

    @Test
    public void projectFinishedProcessingWithoutSubprojects() throws DuplicateException {
        final ProjectDAO dao = new ProjectDAO();
        final Project project = dao.createProject(new CreateProjectParams("user","test"));
        assertFalse(dao.projectFinishedProcessing(String.valueOf(project.getId())));
        dao.updateProjectState(project.getId(), ProjectState.FINISHED);
        assertTrue(dao.projectFinishedProcessing(String.valueOf(project.getId())));
    }

    @Test
    public void projectFinishedProcessingWithSubprojects() throws DuplicateException {
        final ProjectDAO dao = new ProjectDAO();
        final Project project = dao.createProject(new CreateProjectParams("user","test"));
        final List<Project> subprojects = Arrays.asList(
                dao.createProject(CreateProjectParams.subproject(project.getId(), ProjectSubtype.BY_TOPICS)),
                dao.createProject(CreateProjectParams.subproject(project.getId(), ProjectSubtype.CUSTOM_ONLY))
                );

        // Helper
        final Function<Void,Boolean> getFinishedStatus = (a) -> dao.projectFinishedProcessing(String.valueOf(project.getId()));
        final Function<Project,Void> setFinished = (p) -> {
            dao.updateProjectState(p.getId(), ProjectState.FINISHED);
            return null;
        };

        assertFalse(getFinishedStatus.apply(null));
        setFinished.apply(project);
        assertFalse(getFinishedStatus.apply(null));
        setFinished.apply(subprojects.get(0));
        assertFalse(getFinishedStatus.apply(null));
        dao.updateProjectState(subprojects.get(1).getId(), ProjectState.WAITING);
        assertFalse(getFinishedStatus.apply(null));
        setFinished.apply(subprojects.get(1));
        assertTrue(getFinishedStatus.apply(null));
    }


    @Test
    public void rateResultSucceeds() throws MalformedURLException, EntityNotFoundException {

        final ProjectDAO dao = new ProjectDAO();
        final Keyword keyword = createKeyword();
        final University university = new UniversityDAO().addUniversity("test");
        final String url = "https://www.google.com";

        final SearchMetaData smd = new SearchMetaDataDAO().createMetaData(new URL(url),university.getId(), 0L);

        final KeywordScoreDAO keywordScoreDAO = new KeywordScoreDAO();
        final KeyWordScore kws = keywordScoreDAO.createKeywordScore(keyword.getId(),0, "");
        keywordScoreDAO.setMetaData(kws.getId(),smd.getId());

        dao.rateResult(new RateTopicResultDto(String.valueOf(keyword.getTopicId()), url, ResultRelevance.RELEVANT));
        HibernateUtil.execute(session -> {
            final List<ProjectRelevanceScore> scores = session.createQuery("select s from ProjectRelevanceScore  s", ProjectRelevanceScore.class).list();
            assertEquals(1, scores.size());
            assertEquals(ResultRelevance.RELEVANT, scores.get(0).getResultRelevance());
            return null;
        });
    }
}
