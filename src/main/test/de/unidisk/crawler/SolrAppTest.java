package de.unidisk.crawler;

import de.unidisk.common.SystemProperties;
import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.crawler.datatype.Variable;
import de.unidisk.crawler.exception.NoResultsException;
import de.unidisk.crawler.mysql.MysqlConnector;
import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.nlp.basics.EnhancedWithRegExp;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
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
        SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getTestUrl());
        try {
            Stichwort stichwort = new Stichwort("Test");
            QueryResponse response = connector.query(stichwort.buildQuery());
            assertTrue(response.getResults().getNumFound() >= 0);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    public void testFieldInputAndQuery() throws Exception {
        SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getTestUrl());
        List<SolrInputDocument> docs = new ArrayList<>();
        SolrInputDocument document = new SolrInputDocument();
        document.addField(SolrStandardConfigurator.getFieldProperties("id"), "1");
        document.addField(SolrStandardConfigurator.getFieldProperties("title"), "First Document");
        document.addField(SolrStandardConfigurator.getFieldProperties("content"), "Hi, this is the very first document");
        document.addField(SolrStandardConfigurator.getFieldProperties("date"), new Date());
        connector.insertDocument(document);
        docs.add(document.deepCopy());
        document = new SolrInputDocument();
        document.addField(SolrStandardConfigurator.getFieldProperties("id"), "2");
        document.addField(SolrStandardConfigurator.getFieldProperties("title"), "Second Document");
        document.addField(SolrStandardConfigurator.getFieldProperties("content"), "Hi, this is the second document");
        document.addField(SolrStandardConfigurator.getFieldProperties("date"), "2017-03-03T00:00:00Z");
        connector.insertDocument(document);
        docs.add(document.deepCopy());

        Stichwort stichwort = new Stichwort("document");
        QueryResponse response = connector.query(stichwort.buildQuery());
        assertEquals(2, response.getResults().getNumFound());

        stichwort = new Stichwort("doc");
        stichwort.addModifier(EnhancedWithRegExp.Modifier.PART_OF_WORD);
        response = connector.query(stichwort.buildQuery());
        assertEquals(2, response.getResults().getNumFound());

        stichwort = new Stichwort("second");
        response = connector.query(stichwort.buildQuery());
        assertEquals(1, response.getResults().getNumFound());

        Variable variable = new Variable("Test Variable");
        variable.addStichwort(new Stichwort("very"));
        variable.addStichwort(new Stichwort("second"));
        response = connector.query(variable.buildQuery());
        assertEquals(2, response.getResults().getNumFound());

        variable = new Variable("Test Variable");
        variable.addStichwort(new Stichwort("none"));
        variable.addStichwort(new Stichwort("second"));
        response = connector.query(variable.buildQuery());
        assertEquals(1, response.getResults().getNumFound());

        for (SolrInputDocument doc : docs) {
            connector.deleteDocument(doc);
        }
    }

    @Test
    public void testPropertyFiles() throws Exception {
        String[] pathToProperties = {".", "de", "unidisk", "crawler", "unidisk.properties"};
        Properties gitProps = new Properties();
        gitProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(String.join(File.separator, pathToProperties)));

        StringBuilder missingProps = new StringBuilder();
        for (Object prop : gitProps.keySet()) {
            if (systemProperties.getProperty(prop.toString()) == null) {
                missingProps.append(prop.toString()).append(", ");
            }
        }
        assertTrue("Missing properties " + missingProps.toString(), missingProps.length() == 0);
    }
}