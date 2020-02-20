package de.unidisk.crawler;

import de.unidisk.config.CrawlerConfig;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.crawler.simple.ICrawler;
import de.unidisk.crawler.simple.SimpleCrawl;
import de.unidisk.crawler.simple.SimpleSolarSystem;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.common.util.NamedList;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.util.Enumeration;

public class SimpleCrawlTest {

    PodamFactory podamFactory = new PodamFactoryImpl();

    @BeforeClass
    public static void setup() {
       // Logger.getRootLogger().setLevel(Level.INFO);
    }

    /*
     * automatically create a solr collection
     * has protected access use instead:
     * solr create_collection -c myCollection
     */
    @Test
    public void createSimpleSolrCollection() throws IOException, SolrServerException {
        final String solrUrl = SolrConfiguration.getTestUrl();
        HttpSolrClient client =
                new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();

        final SolrRequest request = new CollectionAdminRequest.ClusterStatus();

        final NamedList<Object> response = client.request(request);
        final NamedList<Object> cluster = (NamedList<Object>) response.get("cluster");
        final java.util.List<String> liveNodes = (java.util.List<String>) cluster.get("live_nodes");

        System.out.println("Found " + liveNodes.size() + " live nodes");
    }

    /**
     * test sending a document to solr via api
     * set to ignore because of travis
     * @throws Exception
     */
    @Ignore
    @Test
    public void testFieldInputAndQuery() throws Exception {
        SimpleSolarSystem simpleSolarSystem = new SimpleSolarSystem(SolrConfiguration.getTestUrl());
        simpleSolarSystem.sendPageToTheMoon(podamFactory.manufacturePojo(SimpleSolarSystem.SimpleCrawlDocument.class));
    }

    /**
     * set to ignore because of travis
     * @throws Exception
     */
    //@Ignore
    @Test
    public void shootTheMoon() throws Exception {
        ICrawler crawler = new SimpleCrawl(
                CrawlerConfig.storageLocation,
                CrawlerConfig.seedList,
                CrawlerConfig.whitelist,
                CrawlerConfig.solrUrl
        );
        crawler.startCrawl(CrawlerConfig.seedList[0]);
    }


}
