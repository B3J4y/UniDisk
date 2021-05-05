package de.unidisk.entities.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class KeyWordScore implements ScoredInput {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SearchMetaData searchMetaData;

    @Column()
    private String pageTitle;

    @JsonIgnore
    @ManyToOne
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

    @JsonIgnore
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKeywordId() {
        return keyword.getId();
    }

    @JsonIgnore
    public int getUniversityId() {
        return getSearchMetaData().getUniversity().getId();
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
}
