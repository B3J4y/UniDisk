package de.unidisk.dao;

import de.unidisk.entities.hibernate.Input;
import de.unidisk.entities.hibernate.ScoredInput;
import de.unidisk.entities.hibernate.SearchMetaData;
import org.hibernate.Session;
import org.hibernate.Transaction;

public interface Scoring {
    default ScoredInput addScore(Input input, double score, SearchMetaData smd) {
        Session currentSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();
        ScoredInput scoredInput = queryInput(input, currentSession);

        scoredInput.setScore(score);
        scoredInput.setSearchMetaData(smd);
        currentSession.saveOrUpdate(scoredInput);
        tnx.commit();
        currentSession.close();
        return scoredInput;
    }

    ScoredInput queryInput(Input input, Session currenSesstion);
}
