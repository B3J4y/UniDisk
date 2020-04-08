package de.unidisk.view.results;

import de.unidisk.entities.hibernate.University;

public class Result {
    String topic;
    University university;
    double score;
    long entryCount;

    public Result(String topic, double score, long entryCount, University university) {
        this.topic = topic;
        this.score = score;
        this.entryCount = entryCount;
        this.university = university;
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
}
