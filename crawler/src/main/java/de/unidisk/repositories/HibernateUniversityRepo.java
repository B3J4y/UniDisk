package de.unidisk.repositories;

import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.dao.UniversityDAO;
import de.unidisk.entities.hibernate.University;

import java.util.List;
import java.util.Optional;

public class HibernateUniversityRepo implements IUniversityRepository {

    @Override
    public List<University> getUniversities() {
        return new UniversityDAO().getAll();
    }

    @Override
    public Optional<University> getUniversity(int id) {
        return new UniversityDAO().get(id);
    }
}
