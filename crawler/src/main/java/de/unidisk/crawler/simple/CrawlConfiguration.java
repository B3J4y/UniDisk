package de.unidisk.crawler.simple;

import de.unidisk.config.CrawlerConfiguration;

import java.util.regex.Pattern;

public class CrawlConfiguration {
    private int maxPages;
    private int maxLinkDepth;
    private Pattern fileIgnorePattern;
    private boolean resumeable;

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");


    public CrawlConfiguration(int maxPages, int maxLinkDepth, Pattern fileIgnorePattern, boolean resume) {
        this.maxPages = maxPages;
        this.maxLinkDepth = maxLinkDepth;
        this.fileIgnorePattern = fileIgnorePattern;
    }

    public static CrawlConfiguration fromCrawlerConfiguration(CrawlerConfiguration configuration){
        return new CrawlConfiguration(
                configuration.getMaxVisits(),
                configuration.getMaxDepth(),
                FILTERS,
                configuration.isResumeable()
        );
    }

    public int getMaxPages() {
        return maxPages;
    }

    public int getMaxLinkDepth() {
        return maxLinkDepth;
    }

    public Pattern getFileIgnorePattern() {
        return fileIgnorePattern;
    }

    public boolean isResumeable() {
        return resumeable;
    }
}
