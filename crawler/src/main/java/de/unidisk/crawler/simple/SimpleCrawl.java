package de.unidisk.crawler.simple;

import de.unidisk.crawler.solr.SolrConnector;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;

import static de.unidisk.crawler.simple.SimpleCarlConfig.crawledShitPlace;

public class SimpleCrawl implements ICrawler {

    static private final Logger logger = LogManager.getLogger(SimpleCrawl.class.getName());

    private CrawlController controller;

    public void startCrawl(String seed) throws Exception {

        String crawlStorageFolder = crawledShitPlace;
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        this.controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed(seed);

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<SimpleCarl> factory = () -> new SimpleCarl(seed);

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);

    }

     public void startMultipleCrawl() {
        
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
