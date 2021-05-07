package de.unidisk.crawler.simple;

import de.unidisk.crawler.util.DomainHelper;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UniversityCrawler extends WebCrawler {

    private HashSet<String> whitelistUrls;
    private CrawlConfiguration crawlConfiguration;
    private Function<Page,Void> pageProcessor;

    public UniversityCrawler(String[] whitelist, Function<Page,Void> pageProcessor, CrawlConfiguration configuration) {
        final List<String> domains = Arrays.stream(whitelist).map(DomainHelper::getDomain).filter(Objects::nonNull).collect(Collectors.toList());
        this.whitelistUrls = new HashSet<String>(domains);
        this.crawlConfiguration = configuration;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        boolean ignore = crawlConfiguration.getFileIgnorePattern().matcher(href).matches();
        boolean whitelisted = checkWhiteList(url);
        boolean visitPage =  !ignore
                && whitelisted;
        return visitPage;
    }

    private boolean checkWhiteList(WebURL url) {
        String domain = DomainHelper.getDomain(url.getURL());
        return whitelistUrls.contains(domain);
    }


    @Override
    public void visit(Page page) {
        super.visit(page);
        this.pageProcessor.apply(page);
    }
}
