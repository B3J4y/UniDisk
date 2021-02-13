package de.unidisk.contracts.repositories.params.keyword;

public class CreateKeywordParams {


    String name;
    String topicId;

    public CreateKeywordParams(String name, String topicId) {
        this.name = name;
        this.topicId = topicId;
    }

    public String getName() {
        return name;
    }

    public String getTopicId() {
        return topicId;
    }
}
