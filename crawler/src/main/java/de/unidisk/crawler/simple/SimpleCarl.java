package de.unidisk.crawler.simple;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class SimpleCarl extends WebCrawler {

    private static String whiteList = "uni-potsdam.de";

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    private int maxnNumPages = 5;

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.contains(whiteList);
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

                //if required write content to file
                simpleSolarSystem.sendPageToTheMoon(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }
}
