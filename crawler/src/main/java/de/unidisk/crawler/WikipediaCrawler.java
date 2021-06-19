package de.unidisk.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class WikipediaCrawler {

    static final String[] PAGES = {
            "Biologie",
            "Physik",
            "Informatik",
            "Wissenschaft",
            "Chemie",
            "Geographie",
            "Vulkan",
            "Gestein",
            "Medizin",
            "Psychologie",
            "Geschichte",
            "Sport",
            "Software",
            "Hardware",
            "Zelle",
            "Baukonstruktion",
            "Marketing",
            "Physiologie",
            "Ernährung",
            "Schauspieler",
            "Lehrer",
            "Maschinelles_Lernen",
            "Statistik",
            "Religion",
            "Soziologie",
            "Justiz",
            "Gehirn",
            "Rechtswissenschaft",
            "Obst",
            "Maschinenbau",
            "Politik",
            "Gesetzgebung",
            "Statistik",
            "Netzwerk",
            "Astronomie",
            "Bildung"
    };

    static class Crawler extends WebCrawler {
        static final Pattern PATTERN =  Pattern.compile(".*(\\.(css|js|gif|jpg"
                + "|png|mp3|mp4|zip|gz))$");

        static final Pattern IGNORE_NUMBER_PATTERN = Pattern.compile("^[0-9]+");

        static final String[] IGNORE_START_WORDS = {
                "Gesetz_",
                "Liste_",
                "Senat_",
                "Staatsanwaltschaft_"
        };
        
        static final Pattern IGNORE_START_WORD_PATTERN = Pattern.compile("^("+ String.join("|",IGNORE_START_WORDS) +")");

        static final Pattern[] IGNORE_PATTERNS = {
                IGNORE_NUMBER_PATTERN,

                IGNORE_START_WORD_PATTERN
        };

        @Override
        public boolean shouldVisit(Page referringPage, WebURL url) {
            String href = url.getURL().toLowerCase();
            boolean ignore = PATTERN.matcher(href).matches();
            if(ignore)
                return false;
            if(!url.getURL().startsWith("https://de.wikipedia.org/wiki"))
                return false;

            String[] parts = url.getURL().split("/");
            String page = parts[parts.length -1];

            if(page.toLowerCase().contains("gericht") ||page.contains(":"))
                return false;

            for(Pattern pattern : IGNORE_PATTERNS)
                if(pattern.matcher(page).matches())
                    return false;

            return true;
        }

        @Override
        public void visit(Page page) {
            super.visit(page);
            if (!(page.getParseData() instanceof HtmlParseData))
                return;

            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();

            try {
                String[] parts = page.getWebURL().getURL().split("/");
                FileWriter myWriter = new FileWriter("wikicrawl/pages/"+parts[parts.length - 1]+".html");
                myWriter.write(html);
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        String crawlStorageFolder = "wikicrawl/meta";
        int numberOfCrawlers = 5;
        int maxPages = PAGES.length * 300;

        CrawlConfig config = new CrawlConfig();
        config.setMaxDepthOfCrawling(25);
        config.setMaxPagesToFetch(maxPages);
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setResumableCrawling(true);
        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        CrawlController controllerTemp = null;
        try {
            controllerTemp = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String page : PAGES){
            controllerTemp.addSeed("https://de.wikipedia.org/wiki/"+page);
        }
        CrawlController.WebCrawlerFactory<Crawler> factory = Crawler::new;
        controllerTemp.start(factory, numberOfCrawlers);
        controllerTemp.waitUntilFinish();
    }
}
