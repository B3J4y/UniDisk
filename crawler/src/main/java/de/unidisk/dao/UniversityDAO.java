package de.unidisk.dao;

import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.entities.hibernate.University;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UniversityDAO implements IUniversityRepository {

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
        return HibernateUtil.execute(session -> {
            Optional<University> optUni = session.createQuery("select u from University u where u.name like :name ", University.class)
                    .setParameter("name", university.getName())
                    .uniqueResultOptional();

            if (optUni.isPresent()) {
                return optUni.get();
            }

            session.saveOrUpdate(university);
            return university;
        });
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

    @Override
    public List<University> getUniversities() {
        return HibernateUtil.execute(session -> session.createQuery("from University ", University.class).getResultList());
    }

    @Override
    public List<University> getUniversities(long timeSinceLastCrawl) {
        return HibernateUtil.execute(session -> {
            List<University> universities = session.createQuery("select u from University u where abs(u.lastCrawl - :current ) > :period ", University.class)
                    .setParameter("period",timeSinceLastCrawl)
                    .setParameter("current", System.currentTimeMillis())
                    .getResultList();
            return universities;
        });
    }

    @Override
    public void setLastCrawlTime(int universityId, long timestamp) {
        final Optional<University> uni = get(universityId);
        if(!uni.isPresent()){
            return;
        }

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        final University u = uni.get();
        u.setLastCrawl(timestamp);

        currentSession.update(u);
        transaction.commit();
        currentSession.close();
    }
}
