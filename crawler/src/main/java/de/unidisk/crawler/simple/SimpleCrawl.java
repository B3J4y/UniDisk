package de.unidisk.crawler.simple;

import de.unidisk.crawler.model.CrawlDocument;
import de.unidisk.crawler.model.UniversitySeed;
import de.unidisk.crawler.util.DomainHelper;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class SimpleCrawl implements ICrawler {

    static private final Logger logger = LogManager.getLogger(SimpleCrawl.class.getName());

    private CrawlController controller;

    private String storageLocation;
    private UniversitySeed[] seedList;
    private String[] whiteList;
    private String solrUrl;
    private CrawlConfiguration crawlConfiguration;
    private IProgressListener progressListener;


    public SimpleCrawl(String storageLocation, UniversitySeed[] seeds, String solrUrl, CrawlConfiguration crawlConfiguration){
        this.storageLocation = storageLocation;
        this.seedList = seeds;
        this.whiteList = Arrays.stream(seeds).map(UniversitySeed::getSeedUrl).toArray(String[]::new);
        this.solrUrl = solrUrl;
        this.crawlConfiguration = crawlConfiguration;
    }

    public void startCrawl(String seed) throws Exception {

        final Optional<UniversitySeed> uSeed = Arrays.stream(this.seedList).filter(u -> u.getSeedUrl().equals(seed)).findFirst();
        if(!uSeed.isPresent())
            throw new Exception("No matching university found for given seed");

        startCrawl(uSeed.get(), false);
    }

    private void startCrawl(UniversitySeed seed, Boolean isParallel) throws Exception {
        String crawlStorageFolder = this.storageLocation;
        int numberOfCrawlers = 3;

        CrawlConfig config = new CrawlConfig();
        config.setMaxDepthOfCrawling(crawlConfiguration.getMaxLinkDepth());
        config.setMaxPagesToFetch(crawlConfiguration.getMaxPages());
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setResumableCrawling(this.crawlConfiguration.isResumeable());
        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        if (!isParallel) {
            this.controller = new CrawlController(config, pageFetcher, robotstxtServer);

            // For each crawl, you need to add some seed urls. These are the first
            // URLs that are fetched and then the crawler starts following links
            // which are found in these pages
            controller.addSeed(seed.getSeedUrl());

            // The factory which creates instances of crawlers.
            CrawlController.WebCrawlerFactory<UniversityCrawler> factory = CreateCrawler(seed);

            // Start the crawl. This is a blocking operation, meaning that your code
            // will reach the line after this only when crawling is finished.
            controller.start(factory, numberOfCrawlers);
        } else {
            CrawlController controllerTemp = new CrawlController(config, pageFetcher, robotstxtServer);
            controllerTemp.addSeed(seed.getSeedUrl());
            CrawlController.WebCrawlerFactory<UniversityCrawler> factory = CreateCrawler(seed);
            controllerTemp.start(factory, numberOfCrawlers);
        }
    }

    private CrawlController.WebCrawlerFactory<UniversityCrawler> CreateCrawler(UniversitySeed seed){
        final String domain = DomainHelper.getDomain(seed.getSeedUrl());
        CrawlController.WebCrawlerFactory<UniversityCrawler> factory = () -> new UniversityCrawler(
                whiteList,
                (page) -> processPage(page, domain),
                this.crawlConfiguration
        );
        return factory;
    }



    Void processPage(Page page, String universityDomain){
        if (!(page.getParseData() instanceof HtmlParseData))
            return null;

        final String pageDomain = DomainHelper.getDomain(page.getWebURL().getURL());

        // Prevents cross domain storage 
        // e.g. Uni Potsdam has link to TU Berlin, TU Berlin sites
        // would then be stored with id of UP which would mess up results
        if(!pageDomain.equals(universityDomain)){
            //System.out.println("Skip " + page.getWebURL() + " for university with domain " + universityDomain);
            return null;
        }

        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String html = htmlParseData.getHtml();
        String safe = Jsoup.clean(html, Whitelist.basic());
        final String url = page.getWebURL().toString();
        final String documentId = String.valueOf(url.hashCode());
        final long timestamp = System.currentTimeMillis();
        final String pageTitle = ((HtmlParseData) page.getParseData()).getTitle();
        CrawlDocument simpleCrawlDocument =
                new CrawlDocument(documentId,
                        url, pageTitle,
                        safe, page.getWebURL().getDepth(), timestamp
                );
        try {
            new SimpleSolarSystem(solrUrl).sendPageToTheMoon(simpleCrawlDocument);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
     public void startMultipleCrawl() throws Exception {

         for (UniversitySeed s : seedList) {
             logger.info("STARTING crawl with seed: "+ s);
             if(progressListener !=null)
                 progressListener.onStart(s);

             startCrawl(s.getSeedUrl());
             if(progressListener !=null)
                 progressListener.onSeedFinished(s);
             logger.info("FINISHED crawl with seed: "+ s);
         }
     }

     @Override
     public void startParallelCrawls() {

         for (UniversitySeed s : seedList) {
             Thread t = new Thread(() -> {
                 logger.info("STARTING crawl with seed: "+ s);
                 if(progressListener !=null)
                     progressListener.onStart(s);
                 try {
                     startCrawl(s, true);
                     if(progressListener !=null)
                         progressListener.onSeedFinished(s);
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

    public void setProgressListener(IProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public interface IProgressListener {

        void onStart(UniversitySeed seed);

        void onSeedFinished(UniversitySeed seed);
    }

}
