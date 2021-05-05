package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.entities.hibernate.*;
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


    private Project getParentProject(int projectId, Session session){
        final Project p = session.createQuery("SELECT p from Project p where p.id = :id", Project.class)
                    .setParameter("id",projectId).getSingleResult();
        if(p.getProjectSubtype() == ProjectSubtype.DEFAULT)
                return p;
        return p.getParentProject();
    }
}
