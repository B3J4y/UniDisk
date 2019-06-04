package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.hibernate.SearchMetaData;
import de.unidisk.entities.hibernate.University;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.net.URL;
import java.util.Optional;

public class SearchMetaDataDAO {
    public SearchMetaDataDAO() {
    }

    public SearchMetaData createMetaData(URL url, University university, Long timestamp) {
        Session currentSession = HibernateUtil.getSesstionFactory().getCurrentSession();
        Transaction tnx = currentSession.beginTransaction();
        Optional<SearchMetaData> optSMD = currentSession.createQuery("select s from SearchMetaData s where s.university.name like :name AND s.timestamp = :time AND s.url like :url", SearchMetaData.class)
                .setParameter("name", university.getName())
                .setParameter("time", timestamp)
                .setParameter("url", url.toExternalForm())
                .uniqueResultOptional();
        if (optSMD.isPresent()) {
            tnx.commit();
            currentSession.close();
            return optSMD.get();
        }

        SearchMetaData smd = new SearchMetaData();
        smd.setUrl(url.toExternalForm());
        smd.setUniversity(university);
        smd.setTimestamp(timestamp);
        currentSession.save(smd);
        tnx.commit();
        currentSession.close();
        return smd;
    }
}
