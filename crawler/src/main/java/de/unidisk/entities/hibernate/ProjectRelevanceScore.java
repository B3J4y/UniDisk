package de.unidisk.entities.hibernate;

import javax.persistence.*;

@Entity
public class ProjectRelevanceScore {

    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    SearchMetaData searchMetaData;

    int topicId;

    @Column()
    @Enumerated(EnumType.STRING)
    private ResultRelevance resultRelevance;

    public ProjectRelevanceScore(){}

    public ProjectRelevanceScore(int id, SearchMetaData searchMetaData, int topicId) {
        this.id = id;
        this.searchMetaData = searchMetaData;
        this.topicId = topicId;
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

    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }


    public ResultRelevance getResultRelevance() {
        return resultRelevance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSearchMetaData(SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }


    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setResultRelevance(ResultRelevance resultRelevance) {
        this.resultRelevance = resultRelevance;
    }

    public int getTopicId() {
        return topicId;
    }


}
