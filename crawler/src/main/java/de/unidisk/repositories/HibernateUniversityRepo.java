package de.unidisk.repositories;

import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.dao.UniversityDAO;
import de.unidisk.entities.hibernate.University;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.List;
import java.util.Optional;

@ManagedBean(name = "universityRepo")
@ApplicationScoped
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
