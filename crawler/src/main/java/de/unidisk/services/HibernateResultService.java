package de.unidisk.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.KeyWordScore;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.SearchMetaData;
import de.unidisk.entities.hibernate.Topic;

import javax.ws.rs.ext.Provider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Provider
public class HibernateResultService implements IResultService {

    @Override
    public void createKeywordScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException {
        final KeywordDAO kDao = new KeywordDAO();
        final Optional<Keyword> k = kDao.get(result.getEntityId());
        if(!k.isPresent())
            throw new EntityNotFoundException(Keyword.class,result.getEntityId());

        final Keyword keyword = k.get();
        final KeywordScoreDAO kScoreDao = new KeywordScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();
        final SearchMetaData searchMetaData = searchMetaDataDAO.createMetaData(new URL(result.getUrl()),
                result.getUniversityId(), result.getTimestamp());
        final KeyWordScore score = kScoreDao.createKeywordScore(keyword.getId(),result.getScore(), result.getPageTitle());
        kScoreDao.setMetaData(score.getId(),searchMetaData.getId());
    }

    @Override
    public void createKeywordScores(List<ScoreResult> results) throws EntityNotFoundException, MalformedURLException {
        HibernateUtil.execute(session -> {
            final List<Integer> keywordIds = results.stream().map(ScoreResult::getEntityId).collect(Collectors.toList());
            final List<Keyword> keywords = session.createQuery("select k from Keyword  k where k.id in :ids",Keyword.class
            ).setParameter("ids",keywordIds).list();


            final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();
            final KeywordScoreDAO kScoreDao = new KeywordScoreDAO();
            final int batchSize = 50;

            for(int i= 0; i < results.size();i++){

                final ScoreResult result = results.get(i);
                try {
                    final SearchMetaData searchMetaData = searchMetaDataDAO.createMetaData(new URL(result.getUrl()),
                            result.getUniversityId(), result.getTimestamp(), session
                    );
                    kScoreDao.createKeywordScore(result.getEntityId(),result.getScore(), result.getPageTitle(),searchMetaData,session);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                if ( i % batchSize == 0 ) {
                    //flush a batch of inserts and release memory:
                    session.flush();
                    session.clear();
                }
            }

            return null;
        });
    }

    @Override
    public void createTopicScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException {
        final TopicDAO tDao = new TopicDAO();
        final Optional<Topic> topic = tDao.getTopic(result.getEntityId());
        final boolean topicExists = topic.isPresent();
        if(!topicExists)
            throw new EntityNotFoundException(Topic.class,result.getEntityId());
        final TopicScoreDAO scoreDao = new TopicScoreDAO();
        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();
        final SearchMetaData searchMetaData = searchMetaDataDAO.createMetaData(
                new URL(result.getUrl()),
                result.getUniversityId(),
                result.getTimestamp()
        );
        scoreDao.addScore(topic.get(),result.getScore(),searchMetaData);

    }
}
