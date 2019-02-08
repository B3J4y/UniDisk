package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.Topic;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;


public class TopicDAO {
    public List<Topic> getAll() {
        Session currentSession = HibernateUtil.getSesstionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        List<Topic> topics = currentSession.createQuery("select t from Topic t", Topic.class).list();
        transaction.commit();
        currentSession.close();
        return topics;
    }

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
