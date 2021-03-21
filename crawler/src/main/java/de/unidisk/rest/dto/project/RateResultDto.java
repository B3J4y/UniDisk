package de.unidisk.rest.dto.project;

import de.unidisk.entities.hibernate.ResultRelevance;

public class RateResultDto {

    ResultRelevance relevance;


    public RateResultDto(ResultRelevance relevance) {
        this.relevance = relevance;
    }

    public RateResultDto() {
    }

    public ResultRelevance getRelevance() {
        return relevance;
    }

    public void setRelevance(ResultRelevance relevance) {
        this.relevance = relevance;
    }
}

