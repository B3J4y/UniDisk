package de.unidisk.crawler.simple;

import de.unidisk.crawler.model.CrawlDocument;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;


public class SimpleSolarSystem {

    private String solrUrl;

    public SimpleSolarSystem(String solrUrl) {
        this.solrUrl = solrUrl;
    }

    public void sendPageToTheMoon(CrawlDocument content) throws IOException, SolrServerException {
        HttpSolrClient client =
                new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();

        final UpdateResponse response = client.addBean(content);
        client.commit();
    }


}
