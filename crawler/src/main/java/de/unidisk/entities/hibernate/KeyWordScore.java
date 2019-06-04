package de.unidisk.entities.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class KeyWordScore implements ScoredInput {
    @Id
    private int id;
    @OneToOne
    private SearchMetaData searchMetaData;
    @OneToOne
    private Keyword keyword;
    private double score;

    public KeyWordScore() {
    }

    public KeyWordScore(Keyword keyword) {
        this.keyword = keyword;
    }

    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    @Override
    public Input getInput() {
        return keyword;
    }

    @Override
    public void setSearchMetaData(SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
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
