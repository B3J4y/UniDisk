package de.unidisk.rest;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.rest.authentication.AuthenticatedContext;
import de.unidisk.rest.authentication.ContextUser;
import de.unidisk.rest.dto.keyword.CreateKeywordDto;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.HibernateLifecycle;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateProjectRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class KeywordTest implements HibernateLifecycle {


    IProjectRepository projectRepository;
    ITopicRepository topicRepository;
    IKeywordRepository keywordRepository;

    KeywordRestService keywordRestService;

    SecurityContext context;
    ContextUser user;

    @BeforeEach
     void beforeEach() {
        cleanUpDatabase();
        projectRepository = new HibernateProjectRepo();
        topicRepository = new HibernateTopicRepo();
        keywordRepository = new HibernateKeywordRepo();

        keywordRestService = new KeywordRestService(topicRepository,projectRepository,keywordRepository);
        user = new ContextUser("5","test@web.de");
        context = new AuthenticatedContext(
                user
        );
    }

    Project createOwnProject(String name){
        try {
            return new ProjectDAO().createProject(new CreateProjectParams( user.getId(),name));
        } catch (DuplicateException e) {
            e.printStackTrace();
        }
        return null;
    }

    Project createProject(String name, String userId){
        try {
            return new ProjectDAO().createProject(new CreateProjectParams( userId,name));
        } catch (DuplicateException e) {
            e.printStackTrace();
        }
        return null;
    }

    // CREATE

    @Test
    public void createKeywordWithoutTopic() {
        final CreateKeywordDto dto = new CreateKeywordDto("-1","test");
        final Response r = keywordRestService.create(dto, context);
        Assert.assertEquals( 400,r.getStatus());
    }

    @Test
    public void createKeywordWithOwnProject() throws DuplicateException {
        final Project p = createOwnProject("name");
        final Topic topic = topicRepository.createTopic(p.getId(),"test");

        final CreateKeywordDto dto = new CreateKeywordDto(String.valueOf(topic.getId()),"test");
        final Response r = keywordRestService.create(dto, context);
        Assert.assertEquals( 200,r.getStatus());
    }

    @Test
    public void createTopicWithProjectOfOtherUser() throws DuplicateException {
        final Project p = createProject("name","otherUser");
        final Topic topic = topicRepository.createTopic(p.getId(),"test");

        final CreateKeywordDto dto = new CreateKeywordDto(String.valueOf(topic.getId()),"test");
        final Response r = keywordRestService.create(dto, context);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),r.getStatus());
    }

    // UPDATE


    // DELETE


    @Test
    public void deleteKeywordOfOwnProject() throws DuplicateException {
        final Project project = createOwnProject("2");
        final Topic topic = new TopicDAO().createTopic("topic", project.getId());
        final Keyword keyword = keywordRepository.createKeyword(new CreateKeywordParams("x", String.valueOf(topic.getId())));

        final Response r = keywordRestService.delete(String.valueOf(keyword.getId()), context);
        Assert.assertEquals( 200,r.getStatus());
    }

    @Test
    public void deleteKeywordThatDoesntExist() {
        final Response r = keywordRestService.delete("-1", context);
        Assert.assertEquals( 404,r.getStatus());
    }

    @Test
    public void deleteKeywordOfProjectOfOtherUser() throws DuplicateException {
        final Project p = createProject("name","otherUser");
        final Topic topic = new TopicDAO().createTopic("topic", p.getId());
        final Keyword keyword = keywordRepository.createKeyword(new CreateKeywordParams("x", String.valueOf(topic.getId())));

        final Response r = keywordRestService.delete(String.valueOf(keyword.getId()), context);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),r.getStatus());
    }
}
