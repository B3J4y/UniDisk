package de.unidisk.config;

import de.unidisk.crawler.model.UniversitySeed;

public class CrawlerConfiguration {

    private String storageLocation;
    private int maxDepth;
    private int maxVisits;

    public CrawlerConfiguration(String storageLocation, int maxDepth, int maxVisits) {
        this.storageLocation = storageLocation;
        this.maxDepth = maxDepth;
        this.maxVisits = maxVisits;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxVisits() {
        return maxVisits;
    }
}
