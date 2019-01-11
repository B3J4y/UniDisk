package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.KeyWordScore;
import de.unidisk.entities.Keyword;
import de.unidisk.entities.SearchMetaData;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class KeywordScoreDAO {
    public KeywordScoreDAO() {
    }

    public KeyWordScore addScore(Keyword keyword, double Score, SearchMetaData metaData) {
        Session currentSession = HibernateUtil.getSesstionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();
        KeyWordScore keyWordScore = currentSession.createQuery("select k from KeyWordScore k where k.keyword.name like :name ", KeyWordScore.class)
                .setParameter("name", keyword.getName())
                .uniqueResultOptional()
                .orElse(new KeyWordScore(keyword));

        keyWordScore.setScore(Score);
        keyWordScore.setSearchMetaData(metaData);
        currentSession.saveOrUpdate(keyWordScore);
        tnx.commit();
        currentSession.close();
        return keyWordScore;
    }
}
