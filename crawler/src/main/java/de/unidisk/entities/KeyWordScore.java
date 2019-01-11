package de.unidisk.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class KeyWordScore {
    @Id
    private int id;
    @OneToOne
    private SearchMetaData searchMetaData;
    @OneToOne
    private Keyword keyword;
    private Double score;

    public KeyWordScore() {
    }

    public KeyWordScore(Keyword keyword) {
        this.keyword = keyword;
    }

    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    public void setSearchMetaData(SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
