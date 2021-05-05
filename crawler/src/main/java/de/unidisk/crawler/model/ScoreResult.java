package de.unidisk.crawler.model;

public class ScoreResult {
    int entityId;
    double score;
    int universityId;
    long timestamp;
    String url;
    String pageTitle;

    public ScoreResult(int entityId, double score, int universityId, long timestamp, String url, String pageTitle) {
        this.entityId = entityId;
        this.score = score;
        this.universityId = universityId;
        this.timestamp = timestamp;
        this.url = url;
        this.pageTitle = pageTitle;
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

    public String getPageTitle() {
        return pageTitle;
    }
}
