package de.unidisk.crawler;

import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.crawler.datatype.StichwortModifier;
import de.unidisk.crawler.datatype.Variable;
import de.unidisk.crawler.simple.SimpleCrawl;
import de.unidisk.crawler.simple.SimpleSolarSystem;
import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.nlp.datatype.RegExpStichwort;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SimpleCrawlTest {

    PodamFactory podamFactory = new PodamFactoryImpl();

    /*
     * automatically create a solr collection
     * has protected access use instead:
     * solr create_collection -c myCollection
     */
    @Test
    public void createSimpleSolrCollection() throws IOException, SolrServerException {
        final String solrUrl = "http://localhost:8983/solr";
        HttpSolrClient client =
                new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();

        final SolrRequest request = new CollectionAdminRequest.ClusterStatus();

        final NamedList<Object> response = client.request(request);
        final NamedList<Object> cluster = (NamedList<Object>) response.get("cluster");
        final java.util.List<String> liveNodes = (java.util.List<String>) cluster.get("live_nodes");

        System.out.println("Found " + liveNodes.size() + " live nodes");

        //final SolrRequest request2 = new CollectionAdminRequest.Create("mycollections", "mycollections",1,       1,
                //1, 1);
    }

    /**
     * test sending a document to solr via api
     * @throws Exception
     */
    @Test
    public void testFieldInputAndQuery() throws Exception {
        SimpleSolarSystem simpleSolarSystem = new SimpleSolarSystem();
        simpleSolarSystem.sendPageToTheMoon(podamFactory.manufacturePojo(SimpleSolarSystem.SimpleCarlDocument.class));
    }

    @Test
    public void shootTheMoon() throws Exception {
        SimpleCrawl simpleCrawl = new SimpleCrawl();
        simpleCrawl.crawlCarlCrawl();
    }
}
