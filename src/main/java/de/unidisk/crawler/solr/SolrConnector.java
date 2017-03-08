package de.unidisk.crawler.solr;

import de.unidisk.common.SystemProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by carl on 07.01.16.
 */
public class SolrConnector {
    static private final Logger logger = LogManager.getLogger(SolrConnector.class.getName());
    private SolrClient client ;
    private final String serverUrl;
    private static int limit =1000000;
    private static Properties systemProperties = SystemProperties.getInstance();

    public SolrConnector(String serverUrl) {
        logger.debug("Entering SolrConnector Constructor with serverUrl:" + serverUrl);
        this.serverUrl = serverUrl;
        client = new HttpSolrClient(serverUrl);
        logger.debug("Leaving SolrConnector Constructor");
    }

    public static String getStandardUrl() {
        return "http://" + systemProperties.getProperty("solr.connection.url") + ":" +
                systemProperties.getProperty("solr.connection.port")
                + "/solr/"
                + systemProperties.getProperty("solr.connection.db");
    }

    public static String getTestUrl() {
        return "http://" + systemProperties.getProperty("solr.connection.url") + ":" +
                systemProperties.getProperty("solr.connection.port")
                + "/solr/"
                + systemProperties.getProperty("solr.connection.testDb");
    }

    static public int getLimit() {
        return limit;
    }

    public QueryResponse connectToSolr(String query) throws IOException, SolrServerException {
        //TODO Hacky exception
        if (! query.matches("\\*")) {
            logger.info("Match");
            query = "\"" + query + "\"";
        }
        logger.debug("Entering connectToSolr with query:" + query);
        SolrQuery solrQuery = new SolrQuery("content:" + query);
        solrQuery.set("indent", "true");
        solrQuery.set("rows", limit);
        solrQuery.setFields("id", "content", "title", "score", "url", "pageDepth");
        solrQuery.set("wt", "json");
        if (query.toLowerCase().matches("forsch[a-z]* lern[a-z]*") || query.toLowerCase().matches("entdeckend[a-z]* lern[a-z]*")) {
            solrQuery.set("defType", "edismax");
            solrQuery.set("qs", "10");
        }

        QueryResponse response = client.query(solrQuery);
        SolrDocumentList docs = response.getResults();
        logger.info("Quantitiv Result from " + query + ":" + String.valueOf(docs.getNumFound()));
        if (docs.getNumFound() > limit) {
            logger.warn("Limit Exceeded. Found more Docs in solr than queried. Increase the limit if you want to get more Docs");
        }
        logger.debug("Leaving connectToSolr");
        return response;
    }

    public QueryResponse queryAllDocuments() throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(limit);
        return client.query(solrQuery);
    }

    public void insertDocument(SolrInputDocument document) throws IOException, SolrServerException {
        client.add(document);
        client.commit();
    }

    public void deleteDocument(SolrInputDocument document) throws IOException, SolrServerException {
        client.deleteById(String.valueOf(document.get("id").getValue()));
        client.commit();
    }

    public String getServerUrl(){
        return serverUrl;
    }
}
