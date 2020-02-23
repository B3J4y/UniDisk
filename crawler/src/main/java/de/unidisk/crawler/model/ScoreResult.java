package de.unidisk.crawler.model;

import de.unidisk.entities.hibernate.SearchMetaData;

public class ScoreResult {
    int entityId;
    double score;
    int universityId;
    long timestamp;
    String url;

    public ScoreResult(int entityId, double score, int universityId, long timestamp, String url) {
        this.entityId = entityId;
        this.score = score;
        this.universityId = universityId;
        this.timestamp = timestamp;
        this.url = url;
    }

    public int getEntityId() {
        return entityId;
    }

    public double getScore() {
        return score;
    }

    public int getUniversityId() {
        return universityId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }
}
