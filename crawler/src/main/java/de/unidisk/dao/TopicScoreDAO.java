package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.entities.hibernate.*;
import org.hibernate.Session;

import java.util.List;
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
        return ScoringDAO.super.addScore(input,score,smd);
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
            final Topic topic = score.getTopic();
            final int projectId = topic.getProjectId();

            final List<TopicScore> similarScores = this.findSimilarTopicScoresOfProject(score, session);
            for(TopicScore similarScore : similarScores){
                if(similarScore.getId() == score.getId())
                    continue;

                similarScore.setResultRelevance(relevance);
                session.update(similarScore);
            }

            return score;
        });
        return updatedScore;
    }

    private List<TopicScore> findSimilarTopicScoresOfProject(TopicScore topicScore, Session session) {
        final Topic topic = topicScore.getTopic();
        final int projectId = topic.getProjectId();
        final Project parentProject = getParentProject(projectId,session);

        return session.createQuery("select distinct ts from Project p " +
                "INNER JOIN Project sp ON sp.parentProjectId = p.id "+
                "INNER JOIN Topic t on t.projectId = p.id OR t.projectId = sp.id " +
                "INNER JOIN TopicScore ts on t.id = ts.topic.id " +
                "WHERE t.name = :topic AND (p.id = :projectId OR sp.id = :projectId) " +
                "AND ts.searchMetaData.url = :url", TopicScore.class)
                .setParameter("topic", topicScore.getTopic().getName())
                .setParameter("url", topicScore.getSearchMetaData().getUrl())
                .setParameter("projectId", parentProject.getId()).getResultList();
    }

    private Project getParentProject(int projectId, Session session){
        final Project p = session.createQuery("SELECT p from Project p where p.id = :id", Project.class)
                    .setParameter("id",projectId).getSingleResult();
        if(p.getProjectSubtype() == ProjectSubtype.DEFAULT)
                return p;
        return p.getParentProject();
    }
}
