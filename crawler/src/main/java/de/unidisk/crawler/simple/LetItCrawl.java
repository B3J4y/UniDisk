package de.unidisk.crawler.simple;

public class LetItCrawl implements ICrawler {
    @Override
    public void startCrawl() throws Exception {
        SimpleCrawl simpleCrawl = new SimpleCrawl();
        simpleCrawl.crawlCarlCrawl();
    }
}
