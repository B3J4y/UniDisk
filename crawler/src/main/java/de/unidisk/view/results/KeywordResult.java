package de.unidisk.view.results;

import de.unidisk.entities.hibernate.SearchMetaData;

public class KeywordResult {

    private int keywordId;
    private String keyword;
    private SearchMetaData searchMetaData;
    private double score;
    private String pageTitle;

    public KeywordResult(int keywordId, String keyword, SearchMetaData searchMetaData, double score,
                         String pageTitle
                         ) {
        this.keywordId = keywordId;
        this.keyword = keyword;
        this.searchMetaData = searchMetaData;
        this.score = score;
        this.pageTitle = pageTitle;
    }

    public int getKeywordId() {
        return keywordId;
    }

    public String getKeyword() {
        return keyword;
    }

    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    public double getScore() {
        return score;
    }

    public String getPageTitle() {
        return pageTitle;
    }
}
