package de.unidisk.config;

public class CrawlerConfiguration {

    private String storageLocation;
    private int maxDepth;
    private int maxVisits;
    private long uniCrawlInterval;
    private long crawlInterval;
    private boolean disabled;


    public CrawlerConfiguration(String storageLocation, int maxDepth, int maxVisits, long uniCrawlInterval, long crawlInterval, boolean disabled) {
        this.storageLocation = storageLocation;
        this.maxDepth = maxDepth;
        this.maxVisits = maxVisits;
        this.uniCrawlInterval = uniCrawlInterval;
        this.crawlInterval = crawlInterval;
        this.disabled = disabled;
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


    public boolean isDisabled() {
        return disabled;
    }
}
