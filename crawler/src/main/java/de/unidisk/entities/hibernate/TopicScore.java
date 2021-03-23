package de.unidisk.entities.hibernate;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class TopicScore implements ScoredInput {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private SearchMetaData searchMetaData;

    @OneToOne(cascade= CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Topic topic;
    private double score;

    @Column()
    @Enumerated(EnumType.STRING)
    private ResultRelevance resultRelevance;

    public TopicScore() {
    }

    public TopicScore(SearchMetaData searchMetaData, Topic topic, double score) {
        this.searchMetaData = searchMetaData;
        this.topic = topic;
        this.score = score;
    }

    public TopicScore(Topic topic) {
        this.topic = topic;
    }

    @PrePersist
    public void prePersist() {
        if(resultRelevance == null)
        {
            resultRelevance = ResultRelevance.NONE;
        }
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

    public ResultRelevance getResultRelevance() {
        return resultRelevance;
    }

    public void setResultRelevance(ResultRelevance resultRelevance) {
        this.resultRelevance = resultRelevance;
    }
}
