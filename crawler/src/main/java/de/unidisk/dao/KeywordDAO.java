package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.keyword.UpdateKeywordParams;
import de.unidisk.entities.hibernate.Keyword;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;


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
        return createKeyword(new CreateKeywordParams(name,String.valueOf(topicId),false));
    }

    public Keyword createKeyword(CreateKeywordParams params){
        final int topicId = Integer.parseInt(params.getTopicId());
        final String name = params.getName();

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

        final Keyword keyword = new Keyword(name,topicId, params.isSuggestion());
        currentSession.save(keyword);

        transaction.commit();
        currentSession.close();
        return keyword;
    }

    public Keyword updateKeyword(UpdateKeywordParams params) throws DuplicateException
    {
        final Optional<Keyword> k = get(Integer.parseInt(params.getKeywordId()));
        if (!k.isPresent()) {
            return null;
        }
        final Keyword keyword = k.get();
        keyword.setName(params.getName());

        HibernateUtil.executeUpdate(session -> {
            session.update(keyword);
            return null;
        });

        return keyword;
    }

    public Optional<Keyword> get(int keywordId){
        return HibernateUtil.execute(session -> get(keywordId,session));
    }

    private Optional<Keyword> get(int keywordId, Session session){
        final Optional<Keyword> keyword = session.
                createQuery("select t from Keyword t where t.id = :id", Keyword.class)
                .setParameter("id", keywordId ).uniqueResultOptional();
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

    public void finishedProcessing(int keywordId) throws EntityNotFoundException {
        final EntityNotFoundException result = HibernateUtil.execute(session -> {
            final Optional<Keyword> optionalKeyword = this.get(keywordId,session);
            if(!optionalKeyword.isPresent())
            {
                return new EntityNotFoundException(Keyword.class,keywordId);
            }
            final Keyword keyword = optionalKeyword.get();
            keyword.setFinishedProcessingAt(java.time.Instant.now());
            session.update(keyword);

            return null;
        });

        if(result != null)
            throw result;
    }
}
