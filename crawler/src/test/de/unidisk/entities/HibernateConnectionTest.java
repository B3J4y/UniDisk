package de.unidisk.entities;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.*;

public class HibernateConnectionTest implements HibernateLifecycle {

    @Test
    void createH2Database() throws SQLException, DuplicateException {
        ProjectDAO dao = new ProjectDAO();
        dao.createProject(new CreateProjectParams("test","name"));
        Assert.assertTrue(dao.findProject("name").isPresent());
    }
}
