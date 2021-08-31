package de.unidisk.repositories;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.params.topic.UpdateTopicParams;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.entities.hibernate.TopicScore;

import java.util.List;
import java.util.Optional;


public class HibernateTopicRepo implements ITopicRepository {


    final TopicDAO dao = new TopicDAO();

    @Override
    public Topic createTopic(int projectId, String name) {
        return dao.createTopic(name,projectId);
    }

    @Override
    public Topic updateTopic(UpdateTopicParams params) throws DuplicateException {
        return dao.updateTopic(params);
    }


    @Override
    public void deleteTopic(int topicId) {
        dao.deleteTopic(topicId);
    }

    @Override
    public Keyword addKeyword(int topicId, String name) {
        return new KeywordDAO().addKeyword(name,topicId);
    }

    @Override
    public void deleteKeyword(int keywordId) {
        new KeywordDAO().deleteKeyword(keywordId);
    }

    @Override
    public Optional<Topic> getTopic(int id) {
        return dao.getTopic(id);
    }


    @Override
    public List<TopicScore> getScores(int topicId) throws EntityNotFoundException {
        return dao.getScoresFromKeywords(topicId);
    }

    @Override
    public double getScore(int id) {
        return dao.getScore(id);
    }

    @Override
    public void finishedProcessing(int topicId) throws EntityNotFoundException {
        dao.finishedProcessing(topicId);
    }
}
