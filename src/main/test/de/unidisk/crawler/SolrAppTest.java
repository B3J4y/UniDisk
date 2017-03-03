package de.unidisk.crawler;

import de.unidisk.common.SystemProperties;
import de.unidisk.crawler.exception.NoResultsException;
import de.unidisk.crawler.mysql.MysqlConnector;
import de.unidisk.crawler.solr.SolrConnector;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * Created by carl on 03.03.17.
 */
public class SolrAppTest {
    private static Properties systemProperties = SystemProperties.getInstance();

    @Before
    public void setUp() throws Exception {
        MysqlConnector mc = new MysqlConnector(systemProperties.getProperty("uni.db.name"));
        mc.createNewCampaign("Unit_Test");
    }

    @After
    public void tearDown() throws Exception {
        MysqlConnector mc = new MysqlConnector(systemProperties.getProperty("uni.db.name"));
        mc.deleteCampaing("Unit_Test");
    }

    @Test
    public void smokeTest() {
        MysqlConnector mc = new MysqlConnector(systemProperties.getProperty("uni.db.name"));
        try {
            int campaignStatus = mc.checkCampaignStatus("Unit_Test");
            assertTrue(campaignStatus != 1);
        } catch (NoResultsException e) {
            throw new Error(e);
        }
        SolrConnector connector = new SolrConnector(SolrConnector.getStandardUrl());
        try {
            QueryResponse response = connector.connectToSolr("Test");
            assertTrue(response.getResults().getNumFound() >= 0);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    public void testFieldInputAndQuery() throws Exception {
        SolrConnector connector = new SolrConnector(SolrConnector.getStandardUrl());
        List<SolrInputDocument> docs = new ArrayList<>();
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "1");
        document.addField("name_ss", "First Document");
        document.addField("content", "Hi, this is the very first document");
        document.addField("date_dt", new Date());
        connector.insertDocument(document);
        docs.add(document.deepCopy());
        document = new SolrInputDocument();
        document.addField("id", "2");
        document.addField("name_ss", "Second Document");
        document.addField("content", "Hi, this is the second document");
        document.addField("date_dt", "2017-03-03T00:00:00Z");
        connector.insertDocument(document);
        docs.add(document.deepCopy());
        for (SolrInputDocument doc : docs) {
            connector.deleteDocument(doc);
        }
    }
}