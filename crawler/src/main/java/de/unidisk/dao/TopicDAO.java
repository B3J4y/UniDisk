package de.unidisk.dao;

import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TopicDAO {

    public List<Topic> getAll() {

        List<Topic> topics = execute(session -> {
            final List<Topic> t = session.createQuery("select t from Topic t", Topic.class).list();
            return t;
        });

        return topics;
    }

    <T> T execute(Function<Session,T> action){
        T value = null;
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        value = action.apply(currentSession);
        transaction.commit();
        currentSession.close();
        return value;
    }

    public boolean deleteTopic(int topicId){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

/*
        List<Integer> searchMetaDataIs = currentSession.createQuery("select m.id from TopicScore t INNER JOIN SearchMetaData  m ON t.searchMetaData.id = m.id WHERE t.topic.id = :id", Integer.class)
                .setParameter("id",topicId).getResultList();
        List<Integer> searchMetaDataIds2 = currentSession.createQuery("select ks.id from Keyword k INNER JOIN KeyWordScore ks ON k.id = ks.keyword.id WHERE k.topicId = :id", Integer.class)
                .setParameter("id", topicId).getResultList();
        List<Integer> metaDataIds = Stream.concat(searchMetaDataIs.stream(), searchMetaDataIds2.stream()).collect(Collectors.toList());

        currentSession.createQuery("delete from TopicScore  s where  s.topic.id = :id").setParameter("id", topicId).executeUpdate();*/


        String hql = "delete from Topic where id = :id";
        Query q = currentSession.createQuery(hql).setParameter("id", topicId);
        q.executeUpdate();


        transaction.commit();
        currentSession.close();
        return true;
    }

    public boolean topicExists(int topicId){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Query query = currentSession.
                createQuery("select 1 from Topic t where t.id = :id ");
        query.setParameter("id", topicId );
        final boolean exists = (query.uniqueResult() != null);
        transaction.commit();
        currentSession.close();
        return exists;
    }

    public Topic createTopic(String name, int projectId){
        ProjectDAO p = new ProjectDAO();
        Optional<Project> project =  p.findProjectById(projectId);
        if(!project.isPresent())
            throw new IllegalArgumentException("Project with given id doesn't exist");

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }


        final Topic topic = new Topic(name,projectId);


        currentSession.save(topic);

        transaction.commit();
        currentSession.close();
        return topic;
    }

    public Topic createTopic(String name, int projectId, List<String> keywords){
        final Topic topic = createTopic(name,projectId);

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        topic.setKeywords(keywords.stream().map(k -> new Keyword(k, topic.getId())).collect(Collectors.toList()));
        currentSession.saveOrUpdate(topic);
        transaction.commit();
        currentSession.close();
        return topic;
    }

    public Optional<Topic> findOrCreateTopic(String name) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
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

    public Optional<Topic> getTopic(int id) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Optional<Topic> optTopic = currentSession.createQuery("select t from Topic t where t.id = :id", Topic.class)
                .setParameter("id", id)
                .uniqueResultOptional();

        transaction.commit();
        currentSession.close();
        return optTopic;
    }
}
