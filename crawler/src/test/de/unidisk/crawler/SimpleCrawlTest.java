package de.unidisk.crawler;


import de.unidisk.config.CrawlerConfiguration;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.crawler.model.CrawlDocument;
import de.unidisk.crawler.model.UniversitySeed;
import de.unidisk.crawler.simple.ICrawler;
import de.unidisk.crawler.simple.SimpleCrawl;
import de.unidisk.crawler.simple.SimpleSolarSystem;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.common.util.NamedList;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.net.ConnectException;

import static org.junit.Assert.fail;

public class SimpleCrawlTest {

    PodamFactory podamFactory = new PodamFactoryImpl();
    @BeforeClass
    public static void setup() {

    }

    /**
     * test sending a document to solr via api
     * set to ignore because of travis
     * @throws Exception
     */
    @Test
    @DisabledIf("System.getenv(\"CI\") == '1'")
    public void testFieldInputAndQuery() throws Exception {

        SimpleSolarSystem simpleSolarSystem = new SimpleSolarSystem(SolrConfiguration.getInstance().getUrl());
        simpleSolarSystem.sendPageToTheMoon(podamFactory.manufacturePojo(CrawlDocument.class));
    }

    /**
     * set to ignore because of travis
     * @throws Exception
     */
    @Test
    @Ignore
    public void shootTheMoon() throws Exception {
        final CrawlerConfiguration crawlerConfiguration = SystemConfiguration.getInstance().getCrawlerConfiguration();
        final UniversitySeed[] seeds = new UniversitySeed[]{
                new UniversitySeed("https://www.uni-potsdam.de/de/",0)
        };
        ICrawler crawler = new SimpleCrawl(
                crawlerConfiguration.getStorageLocation(),
                seeds,
                SolrConfiguration.getInstance().getUrl(),
                100
        );
        crawler.startCrawl(seeds[0].getSeedUrl());
    }

}
