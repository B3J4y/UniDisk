package de.unidisk.crawler.datatype;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import de.unidisk.common.SystemProperties;
import de.unidisk.common.mysql.MysqlConnect;
import de.unidisk.crawler.exception.NoDomainFoundException;
import de.unidisk.crawler.exception.NoHochschuleException;
import de.unidisk.crawler.exception.NoResultsException;
import de.unidisk.crawler.model.CrawlDocument;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.solr.SolrConnector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by carl on 06.01.16.
 */
public class Model {
    static private final Logger logger = LogManager.getLogger(Model.class.getName());
    private StichwortVar stichwortVar;
    private VarMeta varMeta;
    private HashMap<String, SolrDocumentList> stichwortResult;
    private String delimiter = ";";

    private String database;
    private Properties systemProperties = SystemProperties.getInstance();


    public Model() throws NoResultsException, CommunicationsException {
        stichwortVar = new StichwortVar();
        varMeta = new VarMeta();
        stichwortResult = new HashMap<>();

        this.database = "";

    }

    public Model(String database) throws NoResultsException, CommunicationsException {
        stichwortVar = new StichwortVar();
        varMeta = new VarMeta();
        stichwortResult = new HashMap<>();
        this.database = database;

    }




    public void scoreVariable(SolrConnector connector, String filepath) throws IOException, SolrServerException, URISyntaxException, InterruptedException {
        logger.debug("Entering scoreVariable with SolrConnector:" + connector.getServerUrl());
        HashMap<String, String> varStich = varMeta.toSolrQuery(stichwortVar);
        int i = 1;
        int size = varStich.keySet().size();
        for (String key: varStich.keySet()) {
            logger.debug("scoreVar Object " + i + " from " + size);
            if (Thread.interrupted()) {
                logger.warn("Thread has been interrupted");
                throw new InterruptedException("Thread interruption forced");
            }
            i++;
            QueryResponse response = connector.query(new SolrStichwort(varStich.get(key)).buildQuery(new ArrayList<>()));
            SolrDocumentList solrList = response.getResults();
            logger.debug("Key:" + varStich.get(key) + " got " + solrList.getNumFound() + " Results");

        }
        logger.debug("Leaving scoreVariable");
    }



}
