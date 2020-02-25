package de.unidisk.crawler.simple;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SimpleCrawl implements ICrawler {

    static private final Logger logger = LogManager.getLogger(SimpleCrawl.class.getName());

    private CrawlController controller;

    private String storageLocation;
    private String[] seedList;
    private String[] whiteList;
    private String solrUrl;
    private int maxVisitsPerSeed;

    public SimpleCrawl(String storageLocation, String[] seedList, String[] whiteList, String solrUrl, int maxVisitsPerSeed){
        this.storageLocation = storageLocation;
        this.seedList = seedList;
        this.whiteList = whiteList;
        this.solrUrl = solrUrl;
        this.maxVisitsPerSeed = maxVisitsPerSeed;
    }

    public void startCrawl(String seed) throws Exception {
        startCrawl(seed, false);
    }

    private void startCrawl(String seed, Boolean isParallel) throws Exception {

        String crawlStorageFolder = this.storageLocation;
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        if (!isParallel) {
            this.controller = new CrawlController(config, pageFetcher, robotstxtServer);

            // For each crawl, you need to add some seed urls. These are the first
            // URLs that are fetched and then the crawler starts following links
            // which are found in these pages
            controller.addSeed(seed);

            // The factory which creates instances of crawlers.
            CrawlController.WebCrawlerFactory<SimpleWebCrawler> factory = CreateCrawler(seed);

            // Start the crawl. This is a blocking operation, meaning that your code
            // will reach the line after this only when crawling is finished.
            controller.start(factory, numberOfCrawlers);
        } else {
            /**
             * the same procedure but with a local controller
             */
            CrawlController controllerTemp = new CrawlController(config, pageFetcher, robotstxtServer);
            controllerTemp.addSeed(seed);
            CrawlController.WebCrawlerFactory<SimpleWebCrawler> factory = CreateCrawler(seed);
            controllerTemp.start(factory, numberOfCrawlers);
        }

    }

    private CrawlController.WebCrawlerFactory<SimpleWebCrawler> CreateCrawler(String seed){
        CrawlController.WebCrawlerFactory<SimpleWebCrawler> factory = () -> new SimpleWebCrawler(seed,whiteList, solrUrl, maxVisitsPerSeed);
        return factory;
    }

    @Override
     public void startMultipleCrawl() throws Exception {
         String[] seedList = this.seedList;
         for (String s : seedList) {
             logger.info("STARTING crawl with seed: "+ s);
             startCrawl(s);
             logger.info("FINISHED crawl with seed: "+ s);
         }
     }

     @Override
     public void startParallelCrawls() {
         String[] seedList = this.seedList;
         for (String s : seedList) {
             Thread t = new Thread(() -> {
                 logger.info("STARTING crawl with seed: "+ s);
                 try {
                     startCrawl(s, true);
                 } catch (Exception e) {
                     logger.error(e);
                 }
                 logger.info("FINISHED crawl with seed: "+ s);
             });
             t.start();
         }
     }

    @Override
    public void stopCrawl() {
        if (controller == null) {
            logger.warn("using stop crawl when now crawl has been started");
        } else {
            controller.shutdown();
        }
    }




}
