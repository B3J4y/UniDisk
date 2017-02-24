package de.unidisk.crawler.solr;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

/**
 * Created by carl on 07.01.16.
 */
public class SolrConnector {
    static private final Logger logger = LogManager.getLogger(SolrConnector.class.getName());
    private SolrClient client ;
    private final String serverUrl;
    private static int limit =1000000;

    public SolrConnector(String serverUrl) {
        logger.debug("Entering SolrConnector Constructor with serverUrl:" + serverUrl);
        this.serverUrl = serverUrl;
        client = new HttpSolrClient(serverUrl);
        logger.debug("Leaving SolrConnector Constructor");
    }

    public SolrConnector(String serverUrl, int limit) {
        logger.debug("Entering SolrConnector Constructor with serverUrl:" + serverUrl);
        this.serverUrl = serverUrl;
        client = new HttpSolrClient(serverUrl);
        SolrConnector.limit = limit;
        logger.debug("Leaving SolrConnector Constructor");
    }
    static public int getLimit() {
        return limit;
    }
    public QueryResponse connectToSolr(String query) throws IOException, SolrServerException {
        //query = "content\"" + StringUtils.join(query.split(" "), "\" AND \"") + "\"";

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

    public String getServerUrl(){
        return serverUrl;
    }
    /*
    private LinkedHashMap<String, ArrayList<LinkedHashMap<String, ArrayList<LinkedHashMap<String, ArrayList<String>>>>>> issueAbstractRequest(String payload) {
        Client client2 = ClientBuilder.newClient();
        WebTarget target2 = client2.target(txUri);
        return target2.request(
                MediaType.APPLICATION_JSON).post(
                Entity.entity(payload,
                        MediaType.APPLICATION_JSON), LinkedHashMap.class);
    }
    */
}
