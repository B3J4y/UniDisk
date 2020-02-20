package de.unidisk.crawler.simple;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;

import static de.unidisk.config.CrawlerConfig.collectionName;
import static de.unidisk.config.CrawlerConfig.solrUrl;

public class SimpleSolarSystem {

    private String solrUrl;

    public SimpleSolarSystem(String solrUrl) {
        this.solrUrl = solrUrl;
    }

    public void sendPageToTheMoon(SimpleCrawlDocument content) throws IOException, SolrServerException {
        HttpSolrClient client =
                new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();

        final UpdateResponse response = client.addBean(collectionName, content);
        client.commit(collectionName);
    }

    public static class SimpleCrawlDocument {
        @Field
        public String id;

        @Field
        public String url;
        @Field
        public String name;

        @Field
        public String title;

        @Field
        public String content;

        @Field
        public Long datum;

        @Field
        public int depth;




        public SimpleCrawlDocument(String id, String url, String title, String content, int depth, Long datum) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.datum = datum;
            this.url = url;
            this.depth = depth;
        }



    }
}
