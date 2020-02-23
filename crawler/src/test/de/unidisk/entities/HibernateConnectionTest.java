package de.unidisk.entities;

import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.*;

public class HibernateConnectionTest implements HibernateLifecycle {

    @Test
    void createH2Database() throws SQLException {
        ProjectDAO dao = new ProjectDAO();
        dao.createProject("name");
        Assert.assertTrue(dao.findProject("name").isPresent());
    }
}
