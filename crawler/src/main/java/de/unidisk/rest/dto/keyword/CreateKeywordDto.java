package de.unidisk.rest.dto.keyword;

public class CreateKeywordDto {

    String topicId;
    String name;
    boolean isSuggestion;

    public  CreateKeywordDto(){}

    public CreateKeywordDto(String topicId, String name) {
        this.topicId = topicId;
        this.name = name;
    }

    public CreateKeywordDto(String topicId, String name, boolean isSuggestion) {
        this.topicId = topicId;
        this.name = name;
        this.isSuggestion = isSuggestion;
    }

    public boolean isSuggestion() {
        return isSuggestion;
    }

    public void setSuggestion(boolean suggestion) {
        isSuggestion = suggestion;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
