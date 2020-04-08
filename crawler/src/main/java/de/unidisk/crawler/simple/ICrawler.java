package de.unidisk.crawler.simple;

public interface ICrawler {
    /**
     * starts a single crawl based on a seed
     * @param seed
     * @throws Exception
     */
    void startCrawl(String seed) throws Exception;

    /**
     * starts sequential crawls for the seed list
     * @throws Exception
     */
    void startMultipleCrawl() throws Exception;

    /**
     * starts parallel crawls for the seed list
     * Note: the parallel crawls cannot be stopped with the stopCrawl command
     */
    void startParallelCrawls();

    void stopCrawl();
}
