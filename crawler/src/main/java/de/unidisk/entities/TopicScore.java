package de.unidisk.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TopicScore {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private SearchMetaData searchMetaData;
    @OneToOne
    private Topic topic;
    private Double score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    public void setSearchMetaData(SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
