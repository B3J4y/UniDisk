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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UniversityCrawler extends WebCrawler {

    /**
     * tree to ensure max depth
     */
    private CarlTree urlTree;
    private String[] whitelistUrls;
    private CrawlConfiguration crawlConfiguration;
    private HashSet<String> visitedPages =  new HashSet<>();
    private Function<Page,Void> pageProcessor;

    public UniversityCrawler(UniversitySeed seed, String[] whitelist, Function<Page,Void> pageProcessor) {
        this.whitelistUrls = whitelist;
        urlTree  = new CarlTree(new CarlsTreeNode(seed.getSeedUrl()));
        this.crawlConfiguration = CrawlConfiguration.Default();
        this.pageProcessor = pageProcessor;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        //Todo: überprüfen ob Fall eintritt
        if(visitedPages.contains(href))
            return false;

        if(visitedPages.size() >= crawlConfiguration.getMaxPages())
            return false;

        boolean visitPage =  !crawlConfiguration.getFileIgnorePattern().matcher(href).matches()
                && checkWhiteList(url);

        if (visitPage) {
            String parent = referringPage.getWebURL().getURL();
            String child = url.getURL();
            CarlsTreeNode parentNode = new CarlsTreeNode(parent);
            CarlsTreeNode childNode = new CarlsTreeNode(child);
            CarlTree copy = urlTree;
            copy.insertCarlsNodes(parentNode, childNode);
            int depth = copy.getPathToRoot(childNode).length;
            childNode.setCarlsDepth(depth);
            if (depth > crawlConfiguration.getMaxLinkDepth()) {
                return false;
            }
            urlTree.insertCarlsNodes(parentNode, childNode);
        }
        visitedPages.add(href);
        return visitPage;
    }

    private boolean checkWhiteList(WebURL url) {
        String urlString = url.toString();
        for (String s : this.whitelistUrls) {
            if (urlString.contains(s)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void visit(Page page) {
        super.visit(page);
        this.pageProcessor.apply(page);
    }
}
