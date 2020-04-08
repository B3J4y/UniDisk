package de.unidisk.crawler.simple;

import java.util.regex.Pattern;

public class CrawlConfiguration {
    private int maxPages;
    private int maxLinkDepth;
    private Pattern fileIgnorePattern;

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    static CrawlConfiguration Default(){
        return new CrawlConfiguration(100,3,FILTERS);
    }

    public CrawlConfiguration(int maxPages, int maxLinkDepth, Pattern fileIgnorePattern) {
        this.maxPages = maxPages;
        this.maxLinkDepth = maxLinkDepth;
        this.fileIgnorePattern = fileIgnorePattern;
    }

    public CrawlConfiguration(int maxPages) {
        this.maxPages = maxPages;
        this.maxLinkDepth = 3;
        this.fileIgnorePattern = FILTERS;
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
}
