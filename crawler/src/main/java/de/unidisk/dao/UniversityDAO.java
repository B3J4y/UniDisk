package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.University;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UniversityDAO {

    public University addUniversity(String name) {
        Session currentSession = HibernateUtil.getSesstionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();
        Optional<University> optUni = currentSession.createQuery("select u from University u where u.name like :name ", University.class)
                .setParameter("name", name)
                .uniqueResultOptional();
        University university;
        if (!optUni.isPresent()) {
            university = new University(name);
            currentSession.saveOrUpdate(university);
            tnx.commit();
        } else {
            university = optUni.get();
        }
        currentSession.close();
        return university;
    }

    public List<University> getAll() {
        Session currentSession = HibernateUtil.getSesstionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();

        List<University> universities = currentSession.createQuery("from University ", University.class).getResultList();
        currentSession.close();
        return universities;
    }
}
