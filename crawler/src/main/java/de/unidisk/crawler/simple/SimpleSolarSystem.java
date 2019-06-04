package de.unidisk.crawler.simple;

import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SimpleSolarSystem {

    public final static String collectionName = "mycollection";
    final String solrUrl = "http://localhost:8983/solr";

    public SimpleSolarSystem() {

    }

    public void sendPageToTheMoon(String content) throws IOException, SolrServerException {

        HttpSolrClient client =
                new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();

        final SimpleSolarSystem.SimpleCarlDocument kindle = new SimpleSolarSystem.SimpleCarlDocument(UUID.randomUUID().toString(),
                content);
        final UpdateResponse response = client.addBean(SimpleSolarSystem.collectionName, kindle);
        client.commit(SimpleSolarSystem.collectionName);
    }

    public static class SimpleCarlDocument {
        @Field
        public String id;
        @Field public String name;

        public SimpleCarlDocument(String id, String name) {
            this.id = id;  this.name = name;
        }

        public SimpleCarlDocument() {}
    }
}
