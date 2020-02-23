package de.unidisk.contracts.repositories;

import de.unidisk.entities.hibernate.University;

import java.util.List;

public interface IUniversityRepository {
    List<University> getUniversities();
}
