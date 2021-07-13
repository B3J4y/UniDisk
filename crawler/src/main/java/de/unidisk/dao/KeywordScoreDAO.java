package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.entities.hibernate.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class KeywordScoreDAO implements ScoringDAO {
    public KeywordScoreDAO() {
    }

    @Override
    public ScoredInput queryInput(Input input, Session currentSession) {
        return currentSession.createQuery("select k from KeyWordScore k where k.keyword.id = :id ", KeyWordScore.class)
                .setParameter("id", input.getId())
                .uniqueResultOptional()
                .orElse(new KeyWordScore((Keyword) input));
    }

    private KeyWordScore findOrFail(int keywordScoreId) throws EntityNotFoundException {
        final Optional<KeyWordScore> score = HibernateUtil.execute(session -> {
            return session.createQuery("select p from KeyWordScore p where p.id = :id", KeyWordScore.class)
                    .setParameter("id", keywordScoreId)
                    .uniqueResultOptional();

        });
        if (!score.isPresent())
            throw new EntityNotFoundException(KeyWordScore.class, keywordScoreId);
        return score.get();
    }

    public void setMetaData(int keywordScoreId, int metaDataId){
        final Optional<SearchMetaData> searchMetaData = new SearchMetaDataDAO().get(metaDataId);
        if(!searchMetaData.isPresent())
            throw new IllegalArgumentException("SearchMetaData not found");
        final Optional<KeyWordScore> optionalScore = get(keywordScoreId);
        if(!optionalScore.isPresent())
            throw new IllegalArgumentException("Score not found");

        final KeyWordScore score = optionalScore.get();

        score.setSearchMetaData(searchMetaData.get());
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        currentSession.saveOrUpdate(score);
        transaction.commit();
        currentSession.close();
    }

    public KeyWordScore createKeywordScore(int keywordId, double score, String pageTitle){
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
        keywordScore.setPageTitle(pageTitle);
        final int id = (int) currentSession.save(keywordScore);

        transaction.commit();
        currentSession.close();
        keywordScore.setId(id);
        return keywordScore;
    }

    public KeyWordScore createKeywordScore(int keywordId, double score, String pageTitle, SearchMetaData searchMetaData, Session session){
        final Keyword keyword = new Keyword();
        keyword.setId(keywordId);

        final KeyWordScore keywordScore = new KeyWordScore();
        keywordScore.setScore(score);
        keywordScore.setKeyword(keyword);
        final String title = pageTitle.length() > 255 ? pageTitle.substring(0,255) : pageTitle;
        keywordScore.setPageTitle(title);
        keywordScore.setSearchMetaData(searchMetaData);
        final int id = (int) session.save(keywordScore);

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
