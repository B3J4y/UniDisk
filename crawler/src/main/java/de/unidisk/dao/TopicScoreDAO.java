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

    public TopicScore createScore(int topicId, double score) throws EntityNotFoundException {
        TopicDAO tDao = new TopicDAO();
        final Optional<Topic> topic = tDao.getTopic(topicId);
        if(!topic.isPresent())
            throw new EntityNotFoundException(Topic.class,topicId);

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        final TopicScore topicScore = new TopicScore();
        topicScore.setScore(score);
        topicScore.setTopic(topic.get());
        final int id = (int) currentSession.save(topicScore);

        transaction.commit();
        currentSession.close();
        topicScore.setId(id);
        return topicScore;
    }
}
