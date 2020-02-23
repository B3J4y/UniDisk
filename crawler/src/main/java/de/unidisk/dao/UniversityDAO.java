package de.unidisk.dao;

import de.unidisk.entities.hibernate.University;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UniversityDAO {

    public University addUniversity(String name) {
        Session currentSession = HibernateUtil.getSessionFactory().openSession();
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

    public University addUniversity(University university) {
        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();
        Optional<University> optUni = currentSession.createQuery("select u from University u where u.name like :name ", University.class)
                .setParameter("name", university.getName())
                .uniqueResultOptional();

        if (!optUni.isPresent()) {

            currentSession.saveOrUpdate(university);
            tnx.commit();
        } else {
            university = optUni.get();
        }
        currentSession.close();
        return university;
    }

    public Optional<University> get(int id){
        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();

        Optional<University> optUniv = currentSession.createQuery("select u from University u where u.id = :id", University.class)
                .setParameter("id", id)
                .uniqueResultOptional();

        currentSession.close();
        return optUniv;
    }

    public boolean exists(int id){
        return get(id).isPresent();
    }

    public List<University> getAll() {
        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();

        List<University> universities = currentSession.createQuery("from University ", University.class).getResultList();
        currentSession.close();
        return universities;
    }
}
