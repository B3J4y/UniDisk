package de.unidisk.crawler.rest.dto.keyword;

public class UpdateKeywordDto {

    String keywordId;
    String name;

    public  UpdateKeywordDto(){}

    public UpdateKeywordDto(String keywordId, String name) {
        this.keywordId = keywordId;
        this.name = name;
    }

    public String getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(String keywordId) {
        this.keywordId = keywordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
