package de.unidisk.contracts.repositories;

import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Topic;

import java.util.List;
import java.util.Optional;

public interface ITopicRepository {

    Topic createTopic(int projectId, String name);

    void deleteTopic(int topicId);

    Keyword addKeyword(int topicId, String name);
    void deleteKeyword(int keywordId);

    Optional<Topic> getTopic(int id);
}
