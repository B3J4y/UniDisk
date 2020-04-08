package de.unidisk.crawler.model;

public class UniversitySeed {
    private String seedUrl;
    private int universityId;

    public UniversitySeed(String seedUrl, int universityId) {
        this.seedUrl = seedUrl;
        this.universityId = universityId;
    }

    public String getSeedUrl() {
        return seedUrl;
    }

    public int getUniversityId() {
        return universityId;
    }
}
