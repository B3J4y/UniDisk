package de.unidisk.contracts.repositories;

import de.unidisk.entities.hibernate.University;

import java.util.List;
import java.util.Optional;

public interface IUniversityRepository {
    List<University> getUniversities();

    Optional<University> getUniversity(int id);
}
