package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.Input;
import de.unidisk.entities.ScoredInput;
import de.unidisk.entities.SearchMetaData;
import org.hibernate.Session;
import org.hibernate.Transaction;

public interface Scoring {
    default ScoredInput addScore(Input input, double score, SearchMetaData smd) {
        Session currentSession = HibernateUtil.getSesstionFactory().openSession();
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
