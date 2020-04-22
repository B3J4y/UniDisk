package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.entities.hibernate.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.unidisk.dao.HibernateUtil.execute;


public class TopicDAO {

    public List<Topic> getAll() {

        List<Topic> topics = execute(session -> {
            final List<Topic> t = session.createQuery("select t from Topic t", Topic.class).list();
            return t;
        });

        return topics;
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
        Optional<Project> project =  p.getProject(String.valueOf(projectId));
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

    public Optional<Topic> getTopic(int id) {
       return execute(session -> {
           Optional<Topic> optTopic = session.createQuery("select t from Topic t where t.id = :id", Topic.class)
                   .setParameter("id", id)
                   .uniqueResultOptional();
           return optTopic;
       });
    }

    public double getScore(int id){
        return execute(session -> {
            double score = (Double) session.createQuery("select sum(k.score)/COUNT (k.id) from KeyWordScore k where keyword.topicId = :id").setParameter("id", id).uniqueResult();
            return score;
        });
    }

    public List<TopicScore> getScoresFromKeywords(int topicId) throws EntityNotFoundException {
        final Optional<Topic> topic = getTopic(topicId);
        if(!topic.isPresent())
            throw new EntityNotFoundException(Topic.class,topicId);

        return execute(session -> {
            //Load required data
            List<Score> scores = session.createQuery("select new de.unidisk.dao.Score((sum(k.score)/COUNT (k.id)), k.searchMetaData.university.id) " +
                    "from KeyWordScore k where k.keyword.topicId = :topicId group by k.searchMetaData.university.id ", Score.class).setParameter("topicId", topicId).list();
            List<Integer> uniIds = scores.stream().map(Score::getUniversityId).collect(Collectors.toList());
            List<University> universities = session.createQuery("select u from University u where u.id in :ids", University.class).setParameter("ids", uniIds).list();

            //put universities into map
            Map<Integer, University> universityMap = universities
                    .stream()
                    .collect(Collectors.toMap(University::getId,Function.identity()));

            //combine data to form results
            return scores.stream().map(score -> new TopicScore(
                    fromUniversity(universityMap.get(score.getUniversityId())),
                    topic.get(),
                    score.getScore()
            )).collect(Collectors.toList());
        });
    }

    private SearchMetaData fromUniversity(University u){
        return new SearchMetaData(
                u.getSeedUrl(),
                u,
                null
        );
    }


}
