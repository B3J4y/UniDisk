package de.unidisk.dao;

import de.unidisk.entities.hibernate.Input;
import de.unidisk.entities.hibernate.ScoredInput;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.entities.hibernate.TopicScore;
import org.hibernate.Session;

public class TopicScoreDAO implements Scoring {
    public TopicScoreDAO() {
    }

    @Override
    public ScoredInput queryInput(Input input, Session currentSession) {
        return currentSession.createQuery("select ts from TopicScore ts where ts.topic.id = :id ", TopicScore.class)
                .setParameter("id", input.getId())
                .uniqueResultOptional()
                .orElse(new TopicScore((Topic) input));
    }
}
