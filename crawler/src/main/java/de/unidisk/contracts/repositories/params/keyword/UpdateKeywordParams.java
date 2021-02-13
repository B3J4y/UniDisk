package de.unidisk.contracts.repositories.params.keyword;

public class UpdateKeywordParams {
    String name;
    String keywordId;

    public UpdateKeywordParams(String name, String keywordId) {
        this.name = name;
        this.keywordId = keywordId;
    }

    public String getName() {
        return name;
    }

    public String getKeywordId() {
        return keywordId;
    }
}
