package de.unidisk.crawler.simple;

import de.unidisk.config.CrawlerConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class SimpleCarl extends WebCrawler {

    /**
     * tree to ensure max depth
     */
    private CarlTree urlTree;

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");
    private String[] whitelistUrls;


    public SimpleCarl(String seed, String[] whitelist) {
        this.whitelistUrls = whitelist;
        urlTree  = new CarlTree(new CarlsTreeNode(seed));
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        boolean result =  !FILTERS.matcher(href).matches()
                && checkWhiteList(url);

        if (result) {
            String parent = referringPage.getWebURL().getURL();
            String child = url.getURL();
            CarlsTreeNode parentNode = new CarlsTreeNode(parent);
            CarlsTreeNode childNode = new CarlsTreeNode(child);
            CarlTree copy = urlTree;
            copy.insertCarlsNodes(parentNode, childNode);
            int depth = copy.getPathToRoot(childNode).length;
            childNode.setCarlsDepth(depth);
            if (depth > 3) {
                return false;
            }
            urlTree.insertCarlsNodes(parentNode, childNode);
        }

        return result;
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
        SimpleSolarSystem simpleSolarSystem = new SimpleSolarSystem();
        try {
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String text = htmlParseData.getText(); //extract text from page
                String html = htmlParseData.getHtml(); //extract html from page
                Set<WebURL> links = htmlParseData.getOutgoingUrls();

                System.out.println("---------------------------------------------------------");
                System.out.println("Text length: " + text.length());
                System.out.println("Html length: " + html.length());
                System.out.println("Number of outgoing links: " + links.size());
                System.out.println("---------------------------------------------------------");

                SimpleSolarSystem.SimpleCarlDocument simpleCarlDocument =
                        new SimpleSolarSystem.SimpleCarlDocument(UUID.randomUUID().toString(), page.getWebURL().toString(),
                                ((HtmlParseData) page.getParseData()).getTitle(), text, page.getWebURL().getDepth(), System
                                .currentTimeMillis
                                ());
                //if required write content to file
                simpleSolarSystem.sendPageToTheMoon(simpleCarlDocument);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }
}
