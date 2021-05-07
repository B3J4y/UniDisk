package de.unidisk.dao;

import de.unidisk.entities.hibernate.SearchMetaData;
import de.unidisk.entities.hibernate.University;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.net.URL;
import java.util.Optional;

public class SearchMetaDataDAO {
    public SearchMetaDataDAO() {
    }

    public SearchMetaData createMetaData(URL url, int universityId, Long timestamp) {
        final Optional<University> university = new UniversityDAO().get(universityId);
        if(!university.isPresent())
            throw new IllegalArgumentException("University " + String.valueOf(universityId) + "  doesn't exist");


        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tnx = currentSession.beginTransaction();

        SearchMetaData smd = new SearchMetaData();
        smd.setUrl(url.toExternalForm());
        smd.setUniversity(university.get());
        smd.setTimestamp(timestamp);
        currentSession.save(smd);
        tnx.commit();
        currentSession.close();
        return smd;
    }

    public Optional<SearchMetaData> get(int id){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        final Optional<SearchMetaData> keyword = currentSession.
                createQuery("select t from SearchMetaData t where t.id = :id", SearchMetaData.class)
                .setParameter("id", id ).uniqueResultOptional();


        transaction.commit();
        currentSession.close();
        return keyword;
    }
    public boolean exists(int id){
        return get(id).isPresent();
    }
}
