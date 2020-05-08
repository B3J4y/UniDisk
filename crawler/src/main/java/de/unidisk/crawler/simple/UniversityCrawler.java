package de.unidisk.crawler.simple;

import de.unidisk.crawler.model.UniversitySeed;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.solr.client.solrj.SolrServerException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UniversityCrawler extends WebCrawler {

    private HashSet<String> whitelistUrls;
    private CrawlConfiguration crawlConfiguration;
    private Function<Page,Void> pageProcessor;

    public UniversityCrawler(String[] whitelist, Function<Page,Void> pageProcessor, CrawlConfiguration configuration) {
        this.whitelistUrls = new HashSet<String>(Arrays.asList(whitelist));
        this.crawlConfiguration = configuration;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        boolean visitPage =  !crawlConfiguration.getFileIgnorePattern().matcher(href).matches()
                && checkWhiteList(url);
        return visitPage;
    }

    private boolean checkWhiteList(WebURL url) {
        String urlString = url.toString();
        return whitelistUrls.contains(urlString);
    }


    @Override
    public void visit(Page page) {
        super.visit(page);
        this.pageProcessor.apply(page);
    }
}
