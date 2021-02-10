package de.unidisk.rest;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.crawler.rest.ProjectServiceRest;
import de.unidisk.crawler.rest.authentication.AuthenticatedContext;
import de.unidisk.crawler.rest.authentication.ContextUser;
import de.unidisk.crawler.rest.dto.project.CreateProjectDto;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.HibernateLifecycle;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.repositories.HibernateProjectRepo;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class ProjectTest implements HibernateLifecycle {


    IProjectRepository projectRepository;
    ProjectServiceRest projectServiceRest;

    SecurityContext context;
    ContextUser user;

    @BeforeEach
    void beforeEach() {
        cleanUpDatabase();
        projectRepository = new HibernateProjectRepo();

        projectServiceRest = new ProjectServiceRest(projectRepository);
        user = new ContextUser("5","test@web.de");
        context = new AuthenticatedContext(
                user
        );
    }
    // CREATE

    @Test
    public void createProject() {
        final CreateProjectDto dto = new CreateProjectDto("test");
        final Response r = projectServiceRest.createProject(dto, context);
        Assert.assertEquals( 200,r.getStatus());
    }

    @Test
    public void createDuplicateProject() {
        final CreateProjectDto dto = new CreateProjectDto("test");
        final Response successResponse = projectServiceRest.createProject(dto, context);
        Assert.assertEquals( 200,successResponse.getStatus());
        final Response duplicateResponse = projectServiceRest.createProject(dto, context);
        Assert.assertEquals( 400,duplicateResponse.getStatus());
    }

    // UPDATE


    // DELETE


    @Test
    public void deleteOwnProject() throws DuplicateException {
        final Project project = new ProjectDAO().createProject(new IProjectRepository.CreateProjectArgs(user.getId(), "test"));
        final Response r = projectServiceRest.deleteProject(String.valueOf(project.getId()), context);
        Assert.assertEquals( 200,r.getStatus());
    }


    @Test
    public void deleteProjectOfOtherUser() throws DuplicateException {
        final Project project = new ProjectDAO().createProject(new IProjectRepository.CreateProjectArgs(user.getId()+"15", "test"));
        final Response r = projectServiceRest.deleteProject(String.valueOf(project.getId()), context);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),r.getStatus());
    }
}
