package de.unidisk.rest.dto.topic;

import de.unidisk.entities.hibernate.ResultRelevance;

public class RateTopicResultDto {

    String topicId;
    String url;
    ResultRelevance relevance;


    public RateTopicResultDto(){

    }

    public RateTopicResultDto(String topicId, String url, ResultRelevance relevance) {
        this.topicId = topicId;
        this.url = url;
        this.relevance = relevance;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getUrl() {
        return url;
    }

    public ResultRelevance getRelevance() {
        return relevance;
    }
}
