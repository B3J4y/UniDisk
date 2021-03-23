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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
