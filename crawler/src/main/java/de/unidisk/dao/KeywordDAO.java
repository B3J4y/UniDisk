package de.unidisk.dao;

import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class KeywordDAO {

    public KeywordDAO() {
    }


    public boolean deleteKeyword(int keywordId){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        String hql = "delete from Keyword where id = :id";
        Query q = currentSession.createQuery(hql).setParameter("id", keywordId);
        q.executeUpdate();


        transaction.commit();
        currentSession.close();
        return true;
    }

    public Keyword addKeyword(String name, int topicId){
        if(keywordExists(topicId,name))
            return null;
        final boolean topicExists = new TopicDAO().topicExists(topicId);
        if(!topicExists)
            throw new IllegalArgumentException("Topic with given id doesn't exist.");

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        final Keyword keyword = new Keyword(name,topicId);
        currentSession.save(keyword);

        transaction.commit();
        currentSession.close();
        return keyword;
    }

    public Optional<Keyword> get(int keywordId){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        final Optional<Keyword> keyword = currentSession.
                createQuery("select t from Keyword t where t.id = :id", Keyword.class)
        .setParameter("id", keywordId ).uniqueResultOptional();


        transaction.commit();
        currentSession.close();
        return keyword;
    }

    public boolean keywordExists(int topicId, String name){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Query query = currentSession.
                createQuery("select 1 from Keyword t where t.name = :name AND t.topicId = :topicId");
        query.setParameter("name", name );
        query.setParameter("topicId", topicId );
        final boolean exists = (query.uniqueResult() != null);
        transaction.commit();
        currentSession.close();
        return exists;
    }

    public boolean keywordExists(int keywordId){
        return get(keywordId).isPresent();
    }


    public List<Keyword> addKeywords(int topicId, List<String> keywordList) {
        List<Keyword> keywords = new ArrayList<>();
        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();
        for (String keywordName : keywordList) {
            Keyword keyword = currentSession.createQuery("select k from Keyword k where k.topicId = :name ", Keyword.class)
                    .setParameter("name", topicId)
                    .uniqueResultOptional()
                    .orElse(new Keyword(keywordName));

            try {
                currentSession.save(keyword);
            } catch (NonUniqueObjectException exp) {

            }
            currentSession.saveOrUpdate(keyword);
            keywords.add(keyword);
        }
        tnx.commit();
        currentSession.close();
        return keywords;
    }

    /**
     * Find keywords by topic name
     */
    public List<Keyword> findKeyWordsByTopic(String topic) {
        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        List<Keyword> keywords = currentSession.createQuery("select k from Keyword k  where k.topicId = :name", Keyword.class)
                .setParameter("name", topic)
                .list();
        transaction.commit();
        currentSession.close();
        return keywords;
    }

    public List<Keyword> findKeywordsByTopics(List<String> topics) {

        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        List<Keyword> keywords = currentSession.createQuery("select k from Keyword k where k.topicId in :topics", Keyword.class)
                .setParameter("topics", topics)
                .list();
        transaction.commit();
        currentSession.close();
        return keywords;
    }

    public List<Keyword> getProjectKeywords(Project p) {

        return findKeywordsByTopics(p.getTopics().stream().map(
                Topic::getName
        ).collect(Collectors.toList()));
    }

    public List<Keyword> getProjectKeywords(int projectId) {

        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        List<Keyword> keywords = currentSession.createQuery("select k from Project p INNER JOIN Topic t ON p.id = t.projectId" +
                " INNER JOIN Keyword k ON k.topicId = t.id where p.id = :projectId", Keyword.class)
                .setParameter("projectId", projectId)
                .list();
        transaction.commit();
        currentSession.close();
        return keywords;
    }
}
