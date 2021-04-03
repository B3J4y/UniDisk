package de.unidisk.entities;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopicScoreTests implements HibernateLifecycle {


    TopicScore setup() throws DuplicateException, MalformedURLException {
        final Project project = new ProjectDAO().createProject(new CreateProjectParams("15","test"));
        final Topic topic = new TopicDAO().createTopic("25",project.getId());
        final University university = new UniversityDAO().addUniversity("test");

        SearchMetaData metaData = new SearchMetaDataDAO().createMetaData(new URL("http://www.uni-potsdam.de/home"), university.getId(),
                ZonedDateTime.now().toEpochSecond());

       return (TopicScore) new TopicScoreDAO().addScore(topic, 1, metaData);
    }

    @Test
    public void rateScoreSucceedsWithExistingScore() throws DuplicateException, MalformedURLException, EntityNotFoundException {
        final TopicScore score = setup();
        final TopicScoreDAO dao = new TopicScoreDAO();
        final TopicScore ratedScore = dao.rateScore(score.getId(), ResultRelevance.RELEVANT);
        assertEquals(ResultRelevance.RELEVANT, ratedScore.getResultRelevance());
    }

    @Test
    public void rateScoreOfSubproject() throws DuplicateException, MalformedURLException, EntityNotFoundException {
        final ProjectDAO projectDAO = new ProjectDAO();
        final TopicDAO topicDAO = new TopicDAO();
        final TopicScoreDAO topicScoreDAO = new TopicScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();
        final University university = new UniversityDAO().addUniversity("test");
        SearchMetaData metaData = searchMetaDataDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), university.getId(),
                ZonedDateTime.now().toEpochSecond());
        final Project project = projectDAO.createProject(new CreateProjectParams("15","test"));
        final Topic topic = topicDAO.createTopic("25",project.getId());

        final ProjectSubtype[] subtypes = new ProjectSubtype[]{ProjectSubtype.BY_TOPICS, ProjectSubtype.CUSTOM_ONLY};
        topicScoreDAO.addScore(topic, 1, metaData);
        final List<TopicScore> topicScores = new ArrayList<>();

        for(ProjectSubtype subtype : subtypes){
            final Project subproject =  projectDAO.createProject(CreateProjectParams.subproject(
                    project.getId(),
                    subtype
            ));
            final Topic[] subprojectTopics = new Topic[]{
                topicDAO.createTopic(topic.getName(),subproject.getId()),
                // This topic should not be affected
                topicDAO.createTopic(topic.getName()+"1",subproject.getId())
            };

            for(int i = 0; i < subprojectTopics.length;i++){
                final Topic t = subprojectTopics[i];
                final TopicScore score = topicScoreDAO.addScore(t, 1, metaData);
                topicScores.add(score);
            }
        }

        final ResultRelevance newRelevance = ResultRelevance.RELEVANT;

        topicScoreDAO.rateScore(topicScores.get(0).getId(), newRelevance);
        List<TopicScore> ratedScores = HibernateUtil.execute(session ->
             session.createQuery("SELECT ts from TopicScore ts where ts.resultRelevance <> :relevance  ", TopicScore.class)
                     .setParameter("relevance", ResultRelevance.NONE).list()
        );
        assertEquals(3, ratedScores.size());
        for(TopicScore score : ratedScores){
            assertEquals(topic.getName(), score.getTopic().getName());
            assertEquals(newRelevance, score.getResultRelevance());
        }
    }
}
