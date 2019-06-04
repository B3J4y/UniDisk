package de.unidisk.entities.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TopicScore implements ScoredInput {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private SearchMetaData searchMetaData;
    @OneToOne
    private Topic topic;
    private double score;

    public TopicScore() {
    }

    public TopicScore(Topic topic) {
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    @Override
    public Input getInput() {
        return topic;
    }

    @Override
    public void setSearchMetaData(SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public void setScore(double score) {
        this.score = score;
    }
}
