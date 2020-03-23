package de.unidisk.repositories;

import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.entities.hibernate.TopicScore;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@ManagedBean(name = "topicRepository")
public class HibernateTopicRepo implements ITopicRepository {


    @Override
    public Topic createTopic(int projectId, String name) {
        return new TopicDAO().createTopic(name,projectId);
    }


    @Override
    public void deleteTopic(int topicId) {
        new TopicDAO().deleteTopic(topicId);
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
        return new TopicDAO().getTopic(id);
    }

    @Override
    public List<TopicScore> getScores(int topicId) {
        return new TopicDAO().getScoresFromKeywords(topicId);
    }

    @Override
    public double getScore(int id) {
        return new TopicDAO().getScore(id);
    }
}
