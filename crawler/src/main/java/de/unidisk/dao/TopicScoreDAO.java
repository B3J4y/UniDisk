package de.unidisk.dao;

import de.unidisk.entities.Input;
import de.unidisk.entities.ScoredInput;
import de.unidisk.entities.Topic;
import de.unidisk.entities.TopicScore;
import org.hibernate.Session;

public class TopicScoreDAO implements Scoring {
    public TopicScoreDAO() {
    }

    @Override
    public ScoredInput queryInput(Input input, Session currentSession) {
        return currentSession.createQuery("select ts from TopicScore ts where ts.topic.name like :name ", TopicScore.class)
                .setParameter("name", input.getName())
                .uniqueResultOptional()
                .orElse(new TopicScore((Topic) input));
    }
}
