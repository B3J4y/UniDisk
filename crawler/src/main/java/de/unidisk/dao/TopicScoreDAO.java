package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.entities.hibernate.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class TopicScoreDAO implements ScoringDAO {
    public TopicScoreDAO() {
    }

    @Override
    public ScoredInput queryInput(Input input, Session currentSession) {
        return currentSession.createQuery("select ts from TopicScore ts where ts.topic.id = :id ", TopicScore.class)
                .setParameter("id", input.getId())
                .uniqueResultOptional()
                .orElse(new TopicScore((Topic) input));
    }

    private TopicScore findOrFail(int topicScoreId) throws EntityNotFoundException {
        final Optional<TopicScore> score = HibernateUtil.execute(session -> {
            return session.createQuery("select p from TopicScore p where p.id = :id", TopicScore.class)
                    .setParameter("id", topicScoreId)
                    .uniqueResultOptional();

        });
        if (!score.isPresent())
            throw new EntityNotFoundException(TopicScore.class, topicScoreId);
        return score.get();
    }

    public TopicScore rateScore(int topicScoreId, ResultRelevance relevance) throws EntityNotFoundException {
        final TopicScore score = this.findOrFail(topicScoreId);
        final TopicScore updatedScore = HibernateUtil.execute(session -> {
            score.setResultRelevance(relevance);
            session.update(score);
            return score;
        });
        return updatedScore;
    }
}
