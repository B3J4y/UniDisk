package de.unidisk.contracts.repositories.params.keyword;

public class CreateKeywordParams {


    String name;
    String topicId;

    boolean isSuggestion;

    public CreateKeywordParams(String name, String topicId) {
        this.name = name;
        this.topicId = topicId;
    }

    public CreateKeywordParams(String name, String topicId,boolean isSuggestion) {
        this.name = name;
        this.topicId = topicId;
        this.isSuggestion = isSuggestion;
    }

    public String getName() {
        return name;
    }

    public String getTopicId() {
        return topicId;
    }

    public boolean isSuggestion() {
        return isSuggestion;
    }
}