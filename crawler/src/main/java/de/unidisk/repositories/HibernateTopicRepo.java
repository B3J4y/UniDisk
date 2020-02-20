package de.unidisk.repositories;

import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.repositories.contracts.ITopicRepository;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ViewScoped
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
}