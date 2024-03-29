package de.unidisk.contracts.repositories;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.topic.UpdateTopicParams;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.entities.hibernate.TopicScore;

import java.util.List;
import java.util.Optional;

public interface ITopicRepository {


    Topic createTopic(int projectId, String name) throws DuplicateException;

    Topic updateTopic(UpdateTopicParams params) throws DuplicateException;

    void deleteTopic(int topicId);

    Keyword addKeyword(int topicId, String name);
    void deleteKeyword(int keywordId);

    Optional<Topic> getTopic(int id);

    List<TopicScore> getScores(int topicId) throws EntityNotFoundException;

    double getScore(int id);

    void finishedProcessing(int topicId) throws EntityNotFoundException;
}
