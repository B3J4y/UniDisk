package de.unidisk.solr;

import de.unidisk.config.SolrConfiguration;
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
    private SolrClient serverClient;
    private final String serverUrl;
    private static int limit =1000000;

    public SolrConnector(SolrConfiguration configuration) {
        logger.debug("Entering SolrConnector Constructor with serverUrl:" + configuration.getServer());
        this.serverUrl = configuration.getCoreUrl();
        client = new HttpSolrClient.Builder(serverUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        serverClient = new HttpSolrClient.Builder(configuration.getServerUrl())
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        logger.debug("Leaving SolrConnector Constructor");
    }


    public QueryResponse query(SolrQuery query) throws IOException, SolrServerException {
        QueryResponse response = client.query(query);
        SolrDocumentList docs = response.getResults();
        logger.info("Quantitiv Result from query \"" + query.getQuery() + "\" is " + docs.getNumFound());
        if (docs.getNumFound() > limit) {
            logger.warn("Limit Exceeded. Found more Docs in solr than queried. Increase the limit if you want to get more Docs");
        }
        return response;
    }

    public SolrClient getClient() {
        return client;
    }


    public SolrClient getServerClient() {
        return serverClient;
    }
}
