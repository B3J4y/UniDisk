package de.unidisk.entities;

import de.unidisk.dao.UniversityDAO;
import de.unidisk.entities.hibernate.University;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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

    @Test
    void existsReturnsTrueIfExists(){
        UniversityDAO universityDAO = new UniversityDAO();
        University uni = universityDAO.addUniversity("FH Potsdam");
        assertTrue(universityDAO.exists(uni.getId()));
    }

    @Test
    void existsReturnsFalseIfDoesntExists(){
        UniversityDAO universityDAO = new UniversityDAO();
        assertFalse(universityDAO.exists(-5));
    }


    @Test
    void findUniversityReturnsMatching(){
        UniversityDAO universityDAO = new UniversityDAO();
        University uni = universityDAO.addUniversity("FH Potsdam");
        final Optional<University> dbUni = universityDAO.get(uni.getId());
        assertTrue(dbUni.isPresent());
        assertEquals(dbUni.get().getName(),uni.getName());
    }
}
