package de.unidisk.entities;

import de.unidisk.dao.ProjectDAO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class ProjectTest implements HibernateLifecycle {
    @Test
    void testCreateProject() {
        ProjectDAO pDAO = new ProjectDAO();
        String pName = "Neues Projekt";
        assertTrue(pDAO.createProject(pName), "New project could not be created");
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
}
