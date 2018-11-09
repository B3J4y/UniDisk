package de.unidisk;

import de.unidisk.rest.TopicData;

import java.util.List;

public class DummyTopicManager implements ITopic {
    @Override
    public void saveTopic(TopicData data) {

    }

    @Override
    public List<TopicData> getTopics() {
        return null;
    }
}
