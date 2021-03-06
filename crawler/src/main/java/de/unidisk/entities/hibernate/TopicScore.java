package de.unidisk.entities.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"searchMetaData_id"}),
        }
)
public class TopicScore implements ScoredInput {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private SearchMetaData searchMetaData;

    @JsonIgnore
    @ManyToOne
    private Topic topic;

    private double score;

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

    @JsonIgnore
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
