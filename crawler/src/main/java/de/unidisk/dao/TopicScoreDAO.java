package de.unidisk.dao;

import de.unidisk.entities.hibernate.Input;
import de.unidisk.entities.hibernate.SearchMetaData;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.entities.hibernate.TopicScore;
import org.hibernate.Session;

import java.util.Optional;

public class TopicScoreDAO implements ScoringDAO<TopicScore> {
    public TopicScoreDAO() {
    }

    @Override
    public TopicScore queryInput(Input input, Session currentSession) {
        return currentSession.createQuery("select ts from TopicScore ts where ts.topic.id = :id ", TopicScore.class)
                .setParameter("id", input.getId())
                .uniqueResultOptional()
                .orElse(new TopicScore((Topic) input));
    }

    @Override
    public TopicScore addScore(Input input, double score, SearchMetaData smd){
        TopicDAO topicDAO = new TopicDAO();

        final Optional<Topic> t = topicDAO.getTopic(input.getId());
        if(!t.isPresent())
            throw new IllegalArgumentException("Topic not found");

        return HibernateUtil.execute(session -> {
            TopicScore topicScore = new TopicScore();
            topicScore.setScore(score);
            topicScore.setTopic(t.get());
            topicScore.setSearchMetaData(smd);
            session.save(topicScore);
            return topicScore;
        });
    }

}
