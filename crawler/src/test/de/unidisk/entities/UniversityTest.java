package de.unidisk.entities;

import de.unidisk.dao.UniversityDAO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UniversityTest implements HibernateLifecycle {
    @Test
    void testCreateUniversity() {
        UniversityDAO universityDAO = new UniversityDAO();
        University up = universityDAO.addUniversity("Uni Potsdam");
        University fu = universityDAO.addUniversity("Freie Universität");

        assertAll("Failed to create universities",
                () -> assertEquals("Uni Potsdam", up.getName(), "failed to create UP"),
                () -> assertEquals("Freie Universität", fu.getName(), "failed to create FU"),
                () -> assertNotEquals(up.getId(), fu.getId(), "Different entities has to have different ids"));
    }

    @Test
    void testAllUniversities() {
        UniversityDAO universityDAO = new UniversityDAO();
        universityDAO.addUniversity("FH Potsdam");
        universityDAO.addUniversity("Technische Universität");

        List<University> allUniversities = universityDAO.getAll();
        assertEquals(2, allUniversities.size(), "Size of universities is wrong");
    }
}