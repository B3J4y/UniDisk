package de.unidisk.rest;

import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.crawler.rest.TopicRestService;
import de.unidisk.crawler.rest.authentication.AuthenticatedContext;
import de.unidisk.crawler.rest.authentication.ContextUser;
import de.unidisk.crawler.rest.dto.topic.CreateTopicDto;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.HibernateLifecycle;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.repositories.HibernateProjectRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class TopicTest implements HibernateLifecycle {


    IProjectRepository projectRepository;
    ITopicRepository topicRepository;

    TopicRestService topicRestService;

    SecurityContext context;
    ContextUser user;

    @BeforeEach
     void beforeEach() {
        cleanUpDatabase();
        projectRepository = new HibernateProjectRepo();
        topicRepository = new HibernateTopicRepo();

        topicRestService = new TopicRestService(projectRepository,topicRepository);
        user = new ContextUser("5","test@web.de");
        context = new AuthenticatedContext(
                user
        );
    }

    Project createOwnProject(String name){
        return new ProjectDAO().createProject(new IProjectRepository.CreateProjectArgs( user.getId(),name));
    }

    Project createProject(String name, String userId){
        return new ProjectDAO().createProject(new IProjectRepository.CreateProjectArgs( userId,name));
    }

    // CREATE

    @Test
    public void createTopicWithoutProject() {
        final CreateTopicDto dto = new CreateTopicDto("-1","test");
        final Response r = topicRestService.create(dto, context);
        Assert.assertEquals( 400,r.getStatus());
    }

    @Test
    public void createTopicWithOwnProject() {
        final Project p = createOwnProject("name");
        final CreateTopicDto dto = new CreateTopicDto(String.valueOf(p.getId()),"test");
        final Response r = topicRestService.create(dto, context);
        Assert.assertEquals( 200,r.getStatus());
    }

    @Test
    public void createTopicWithProjectOfOtherUser() {
        final Project p = createProject("name","otherUser");
        final CreateTopicDto dto = new CreateTopicDto(String.valueOf(p.getId()),"test");
        final Response r = topicRestService.create(dto, context);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),r.getStatus());
    }

    // UPDATE


    // DELETE


    @Test
    public void deleteTopicOfOwnProject() {
        final Project project = createOwnProject("2");
        final Topic topic = new TopicDAO().createTopic("topic", project.getId());
        final Response r = topicRestService.delete(String.valueOf(topic.getId()), context);
        Assert.assertEquals( 200,r.getStatus());
    }

    @Test
    public void deleteTopicThatDoesntExist() {
        final Response r = topicRestService.delete("-1", context);
        Assert.assertEquals( 404,r.getStatus());
    }

    @Test
    public void deleteTopicOfProjectOfOtherUser() {
        final Project p = createProject("name","otherUser");
        final Topic topic = new TopicDAO().createTopic("topic", p.getId());
        final Response r = topicRestService.delete(String.valueOf(topic.getId()), context);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),r.getStatus());
    }
}
