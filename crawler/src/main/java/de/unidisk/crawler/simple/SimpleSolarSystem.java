package de.unidisk.crawler.simple;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;

import static de.unidisk.config.CrawlerConfig.collectionName;
import static de.unidisk.config.CrawlerConfig.solrUrl;

public class SimpleSolarSystem {

    public SimpleSolarSystem() {

    }

    public void sendPageToTheMoon(SimpleCarlDocument content) throws IOException, SolrServerException {
        HttpSolrClient client =
                new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();

        final UpdateResponse response = client.addBean(collectionName, content);
        client.commit(collectionName);
    }

    public static class SimpleCarlDocument {
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




        public SimpleCarlDocument(String id, String url, String title, String content, int depth, Long datum) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.datum = datum;
            this.url = url;
            this.depth = depth;
        }



    }
}
