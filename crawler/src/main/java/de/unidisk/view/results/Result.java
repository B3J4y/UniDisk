package de.unidisk.view.results;

public class Result {
    String topic;
    double score;
    int entryCount;

    public Result(String topic, double score, int entryCount) {
        this.topic = topic;
        this.score = score;
        this.entryCount = entryCount;
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

    public int getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(int entryCount) {
        this.entryCount = entryCount;
    }
}
