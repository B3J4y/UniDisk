package de.unidisk.view.results;

import de.unidisk.entities.hibernate.ResultRelevance;
import de.unidisk.entities.hibernate.University;

public class Result {
    int id;
    int topicId;
    String topic;
    University university;
    double score;
    long entryCount;
    ResultRelevance relevance;

    public Result(int id, int topicId, String topic, double score, long entryCount, University university) {
        this.id = id;
        this.topicId = topicId;
        this.topic = topic;
        this.score = score;
        this.entryCount = entryCount;
        this.university = university;
        this.relevance = ResultRelevance.NONE;
    }

    public Result(int id ,int topicId, String topic, double score, long entryCount, University university, ResultRelevance relevance) {
        this.id = id;
        this.topicId = topicId;
        this.topic = topic;
        this.university = university;
        this.score = score;
        this.entryCount = entryCount;
        this.relevance = relevance;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(long entryCount) {
        this.entryCount = entryCount;
    }

    public University getUniversity() {
        return university;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRelevance(ResultRelevance relevance) {
        this.relevance = relevance;
    }

    public ResultRelevance getRelevance() {
        return relevance;
    }
}
