package de.unidisk.dao;

import de.unidisk.entities.hibernate.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashMap;
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

    public double getScore(int id){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        double score = (Double) currentSession.createQuery("select sum(k.score)/COUNT (k.id) from KeyWordScore k where keyword.topicId = :id").setParameter("id",id).uniqueResult();

        transaction.commit();
        currentSession.close();
        return score;
    }

    public List<TopicScore> getScoresFromKeywords(int topicId){
        final Optional<Topic> t = getTopic(topicId);
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        List<Score> scores= currentSession.createQuery("select new de.unidisk.dao.Score((sum(k.score)/COUNT (k.id)), k.searchMetaData.university.id) " +
                "from KeyWordScore k where k.keyword.topicId = :topicId group by k.searchMetaData.university.id ").setParameter("topicId",topicId).list();
        List<Integer> uniIds = scores.stream().map(s -> s.getUniversityId()).collect(Collectors.toList());
        List<University> universities = currentSession.createQuery("select u from University u where u.id in :ids").setParameter("ids", uniIds).list();
        HashMap<Integer,University> universityHashMap = new HashMap<Integer,University>();
        for(University u : universities)
            universityHashMap.put(u.getId(),u);

        transaction.commit();
        currentSession.close();
        return scores.stream().map(s -> new TopicScore(
                fromUniversity(universityHashMap.get(s.getUniversityId())),
                t.get(),
                s.getScore()
        )).collect(Collectors.toList());
    }

    private SearchMetaData fromUniversity(University u){
        return new SearchMetaData(
                u.getSeedUrl(),
                u,
                null
        );
    }


}
