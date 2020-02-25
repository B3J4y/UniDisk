package de.unidisk.crawler.simple;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class SimpleWebCrawler extends WebCrawler {

    /**
     * tree to ensure max depth
     */
    private CarlTree urlTree;

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");
    private String[] whitelistUrls;
    private int maxCrawlDepth = 3;
    private String solrUrl;
    private int maxPages;
    private HashSet<String> visitedPages =  new HashSet<>();

    public SimpleWebCrawler(String seed, String[] whitelist, String solrUrl, int maxPages) {
        this.whitelistUrls = whitelist;
        urlTree  = new CarlTree(new CarlsTreeNode(seed));
        this.solrUrl = solrUrl;
        this.maxPages = maxPages;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if(visitedPages.contains(href))
            return false;

        if(visitedPages.size() >= maxPages)
            return false;

        boolean visitPage =  !FILTERS.matcher(href).matches()
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
            if (depth > maxCrawlDepth) {
                return false;
            }
            urlTree.insertCarlsNodes(parentNode, childNode);
        }
        visitedPages.add(href);
        return visitPage;
    }

    public Boolean checkWhiteList(WebURL url) {
        String urlString = url.toString();
        boolean result = false;
        for (String s : this.whitelistUrls) {
            if (urlString.contains(s)){
                result = true;
                break;
            }
        }
        return result;
    }


    @Override
    public void visit(Page page) {
        super.visit(page);
        SimpleSolarSystem simpleSolarSystem = new SimpleSolarSystem(solrUrl);
        try {
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String text = htmlParseData.getText();
                String html = htmlParseData.getHtml();
                Set<WebURL> links = htmlParseData.getOutgoingUrls();

                System.out.println("---------------------------------------------------------");
                System.out.println("Text length: " + text.length());
                System.out.println("Html length: " + html.length());
                System.out.println("Number of outgoing links: " + links.size());
                System.out.println("---------------------------------------------------------");

                final String url = page.getWebURL().toString();
                final String documentId = String.valueOf(url.hashCode());
                final long timestamp = System.currentTimeMillis();
                final String pageTitle = ((HtmlParseData) page.getParseData()).getTitle();
                SimpleSolarSystem.SimpleCrawlDocument simpleCrawlDocument =
                        new SimpleSolarSystem.SimpleCrawlDocument(documentId,
                                url, pageTitle,
                                text, page.getWebURL().getDepth(), timestamp);
                //if required write content to file
                simpleSolarSystem.sendPageToTheMoon(simpleCrawlDocument);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }
}
