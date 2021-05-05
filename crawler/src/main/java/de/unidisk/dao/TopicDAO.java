package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.topic.UpdateTopicParams;
import de.unidisk.entities.hibernate.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.*;
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

    public Topic updateTopic(UpdateTopicParams params) throws DuplicateException {
        final Optional<Topic> t = getTopic(params.getTopicId());
        if (!t.isPresent()) {
            return null;
        }
        final Topic topic = t.get();
        topic.setName(params.getName());

        HibernateUtil.executeUpdate(session -> {
            session.update(topic);
            return null;
        });
        return topic;
    }

    public Optional<Topic> getProjectTopicByName(String projectId, String topic){
        return execute(session -> {
            Optional<Topic> optTopic = session.createQuery("select t from Topic t where t.name = :name AND t.projectId = :projectId", Topic.class)
                    .setParameter("projectId", Integer.parseInt(projectId))
                    .setParameter("name",topic)
                    .uniqueResultOptional();
            return optTopic;
        });
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
            List<KeyWordScore> scores = session.createQuery("select k " +
                    "from KeyWordScore k where k.keyword.topicId = :topicId", KeyWordScore.class).setParameter("topicId", topicId).list();
            Map<Integer,List<KeyWordScore>> universityScores = new HashMap<>();

            scores.forEach(score -> {
                if(universityScores.containsKey(score.getUniversityId())){
                    universityScores.get(score.getUniversityId()).add(score);
                }else {
                    List<KeyWordScore> scoreList = new ArrayList<>();
                    scoreList.add(score);
                    universityScores.put(score.getUniversityId(),scoreList);
                }
            });

            Map<Integer,University> universityMap =  scores.stream().collect(Collectors.toMap(KeyWordScore::getUniversityId, score -> score.getSearchMetaData().getUniversity(), (v1, v2) -> v1));



            return universityScores.keySet().stream().map(universityId -> {
                List<KeyWordScore> scoreList = universityScores.get(universityId);
                final double score = scoreList.stream().map(KeyWordScore::getScore).reduce(0.0, Double::sum)/scoreList.size();
                University university = universityMap.get(universityId);

                final SearchMetaData searchMetaData = fromUniversity(university);
                return new TopicScore(
                        searchMetaData,
                        topic.get(),
                        score
                );
            }).collect(Collectors.toList());
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
