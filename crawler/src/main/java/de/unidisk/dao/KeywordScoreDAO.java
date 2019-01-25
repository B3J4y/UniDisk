package de.unidisk.dao;

import de.unidisk.entities.Input;
import de.unidisk.entities.KeyWordScore;
import de.unidisk.entities.Keyword;
import de.unidisk.entities.ScoredInput;
import org.hibernate.Session;

public class KeywordScoreDAO implements Scoring {
    public KeywordScoreDAO() {
    }

    @Override
    public ScoredInput queryInput(Input input, Session currentSession) {
        return currentSession.createQuery("select k from KeyWordScore k where k.keyword.name like :name ", KeyWordScore.class)
                .setParameter("name", input.getName())
                .uniqueResultOptional()
                .orElse(new KeyWordScore((Keyword) input));
    }
}
