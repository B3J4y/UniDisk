package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.Topic;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;


public class TopicDAO {
    public Optional<Topic> findOrCreateTopic(String name) {
        Session currentSession = HibernateUtil.getSesstionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Optional<Topic> optTopic = currentSession.createQuery("select t from Topic t where t.name like :name", Topic.class)
                .setParameter("name", name)
                .uniqueResultOptional();

        transaction.commit();
        currentSession.close();
        return optTopic;
    }
}
