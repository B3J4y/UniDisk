package de.unidisk.dao;

import de.unidisk.entities.hibernate.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class KeywordScoreDAO implements Scoring {
    public KeywordScoreDAO() {
    }

    @Override
    public ScoredInput queryInput(Input input, Session currentSession) {
        return currentSession.createQuery("select k from KeyWordScore k where k.keyword.id = :id ", KeyWordScore.class)
                .setParameter("id", input.getId())
                .uniqueResultOptional()
                .orElse(new KeyWordScore((Keyword) input));
    }

    public void setMetaData(int keywordScoreId, int metaDataId){
        final Optional<SearchMetaData> searchMetaData = new SearchMetaDataDAO().get(metaDataId);
        if(!searchMetaData.isPresent())
            throw new IllegalArgumentException("SearchMetaData not found");
        final Optional<KeyWordScore> score = get(keywordScoreId);
        if(!score.isPresent())
            throw new IllegalArgumentException("Score not found");
        score.get().setSearchMetaData(searchMetaData.get());
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        currentSession.saveOrUpdate(score.get());
        transaction.commit();
        currentSession.close();
    }

    public KeyWordScore createKeywordScore(int keywordId, double score){
        KeywordDAO kDao = new KeywordDAO();
        final Optional<Keyword> keyword = kDao.get(keywordId);
        if(!keyword.isPresent())
            throw new IllegalArgumentException("Keyword not found");

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        final KeyWordScore keywordScore = new KeyWordScore();
        keywordScore.setScore(score);
        keywordScore.setKeyword(keyword.get());
        final int id = (int) currentSession.save(keywordScore);

        transaction.commit();
        currentSession.close();
        keywordScore.setId(id);
        return keywordScore;
    }

    public Optional<KeyWordScore> get(int id){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        Optional<KeyWordScore> optProj = currentSession.createQuery("select p from KeyWordScore p where p.id =:id", KeyWordScore.class)
                .setParameter("id", id)
                .uniqueResultOptional();

        transaction.commit();
        currentSession.close();
        return optProj;
    }
}
