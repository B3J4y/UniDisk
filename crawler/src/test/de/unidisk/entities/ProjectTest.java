package de.unidisk.entities;

import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class ProjectTest implements HibernateLifecycle {
    private String pName = "Neues Projekt";
    private String secondTopic = "Hallo Du";

    private List<Pair<String, String>> createTwoTopics() {
        List<Pair<String, String>> keyTop = new ArrayList<>();
        String strHallo = "Hallo";
        keyTop.add(Pair.of(strHallo, getHWTopicName()));
        keyTop.add(Pair.of("Welt", getHWTopicName()));
        keyTop.add(Pair.of(strHallo, secondTopic));
        keyTop.add(Pair.of("Du", secondTopic));
        return keyTop;
    }

    @Test
    void testCreateProject() {
        ProjectDAO pDAO = new ProjectDAO();
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

    @Test
    void testAllProjects() {
        KeywordDAO kDao = new KeywordDAO();
        List<Pair<String, String>> keyTop = createTwoTopics();
        kDao.addKeywords(keyTop);

        ProjectDAO pDAO = new ProjectDAO();
        assertTrue(pDAO.createProject(pName), "New project could not be created");
        assertTrue(pDAO.createProject(pName + "2"), "New project could not be created");
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
        assertTrue(pDAO.createProject(pName + "3"), "New project could not be created");
        assertEquals(3, pDAO.getAll().size(), "There have to be 3 projects");
    }
}
