package de.unidisk.view.model;

import de.unidisk.entities.hibernate.Keyword;

public class KeywordItem {
    private String id;
    private String keyword;
    private String variable;

    public KeywordItem(String id, String keyword, String variable) {
        this.id = id;
        this.keyword = keyword;
        this.variable = variable;
    }

    public KeywordItem copy(String id){
        return new KeywordItem(
                id,
                this.keyword,
                this.variable
        );
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
}
