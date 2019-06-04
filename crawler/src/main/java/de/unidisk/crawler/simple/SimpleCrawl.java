package de.unidisk.crawler.simple;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.ArrayList;

import static de.unidisk.crawler.simple.SimpleCarlConfig.crawledShitPlace;

public class SimpleCrawl {




    public void crawlCarlCrawl() throws Exception {

        String crawlStorageFolder = crawledShitPlace;
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        String[] seedList = SimpleCarlConfig.seedList;
        for (String s : seedList) {
            controller.addSeed(s);
        }

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<SimpleCarl> factory = SimpleCarl::new;

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);


    }
}
