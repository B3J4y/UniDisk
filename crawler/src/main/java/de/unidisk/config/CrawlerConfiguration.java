package de.unidisk.config;

import de.unidisk.crawler.model.UniversitySeed;

public class CrawlerConfiguration {

    private String storageLocation;
    private int maxDepth;
    private int maxVisits;
    private long uniCrawlInterval;
    private long crawlInterval;


    public CrawlerConfiguration(String storageLocation, int maxDepth, int maxVisits, long uniCrawlInterval, long crawlInterval) {
        this.storageLocation = storageLocation;
        this.maxDepth = maxDepth;
        this.maxVisits = maxVisits;
        this.uniCrawlInterval = uniCrawlInterval;
        this.crawlInterval = crawlInterval;
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

    public long getUniCrawlInterval() {
        return uniCrawlInterval;
    }

    public long getCrawlInterval() {
        return crawlInterval;
    }
}
