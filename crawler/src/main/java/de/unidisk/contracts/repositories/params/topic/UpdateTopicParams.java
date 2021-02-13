package de.unidisk.contracts.repositories.params.topic;

public class UpdateTopicParams {
    final int topicId;
    final String name;

    public UpdateTopicParams(int topicId, String name) {
        this.topicId = topicId;
        this.name = name;
    }

    public int getTopicId() {
        return topicId;
    }

    public String getName() {
        return name;
    }
}
