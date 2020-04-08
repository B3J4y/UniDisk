package de.unidisk.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import de.unidisk.contracts.services.IResultService;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@ManagedBean(name = "resultService")
@ApplicationScoped
public class HibernateResultService implements IResultService {

    @Override
    public void CreateKeywordScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException {
        final KeywordDAO kDao = new KeywordDAO();
        final Optional<Keyword> k = kDao.get(result.getEntityId());
        if(!k.isPresent())
            throw new EntityNotFoundException(Keyword.class,result.getEntityId());

        final Keyword keyword = k.get();
        final KeywordScoreDAO kScoreDao = new KeywordScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();
        final SearchMetaData searchMetaData = searchMetaDataDAO.createMetaData(new URL(result.getUrl()),
                result.getUniversityId(), result.getTimestamp());
        final KeyWordScore score = kScoreDao.createKeywordScore(keyword.getId(),result.getScore());
        kScoreDao.setMetaData(score.getId(),searchMetaData.getId());
    }

    @Override
    public void CreateTopicScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException {
        final TopicDAO tDao = new TopicDAO();
        final Optional<Topic> topic = tDao.getTopic(result.getEntityId());
        final boolean topicExists = topic.isPresent();
        if(!topicExists)
            throw new EntityNotFoundException(Topic.class,result.getEntityId());
        final TopicScoreDAO scoreDao = new TopicScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();
        final SearchMetaData searchMetaData = searchMetaDataDAO.createMetaData(new URL(result.getUrl()),
                result.getUniversityId(), result.getTimestamp());
        scoreDao.addScore(topic.get(),result.getScore(),searchMetaData);

    }
}
