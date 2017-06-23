package de.unidisk.crawler;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import de.unidisk.common.SystemProperties;
import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.crawler.datatype.StichwortModifier;
import de.unidisk.crawler.datatype.Variable;
import de.unidisk.crawler.exception.NoResultsException;
import de.unidisk.crawler.mysql.MysqlConnector;
import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.nlp.datatype.RegExpStichwort;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by carl on 03.03.17.
 */
public class SolrAppTest {
    private static Properties systemProperties = SystemProperties.getInstance();

    @Before
    public void setUp() throws Exception {
        MysqlConnector mc = new MysqlConnector();
        mc.createNewCampaign("Unit_Test");
    }

    @After
    public void tearDown() throws Exception {
        MysqlConnector mc = new MysqlConnector();
        mc.deleteCampaing("Unit_Test");
    }

    @Test
    public void smokeTest() throws CommunicationsException {
        MysqlConnector mc = new MysqlConnector();
        try {
            int campaignStatus = mc.checkCampaignStatus("Unit_Test");
            assertTrue(campaignStatus != 1);
        } catch (NoResultsException e) {
            throw new Error(e);
        }
        SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getTestUrl(),
                systemProperties.getProperty("solr.connection.testDb"));
        try {
            Stichwort stichwort = new RegExpStichwort("Test");
            QueryResponse response = connector.query(stichwort.buildQuery(new ArrayList<>()));
            assertTrue(response.getResults().getNumFound() >= 0);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    public void testFieldInputAndQuery() throws Exception {
        SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getTestUrl(),
                systemProperties.getProperty("solr.connection.testDb"));
        List<SolrInputDocument> docs = new ArrayList<>();
        SolrInputDocument document = new SolrInputDocument();
        document.addField(SolrStandardConfigurator.getFieldProperty("id"), "1");
        document.addField(SolrStandardConfigurator.getFieldProperty("title"), "First Document");
        document.addField(SolrStandardConfigurator.getFieldProperty("content"), "Hi, this is the very first document");
        document.addField(SolrStandardConfigurator.getFieldProperty("date"), new Date());
        connector.insertDocument(document);
        docs.add(document.deepCopy());
        document = new SolrInputDocument();
        document.addField(SolrStandardConfigurator.getFieldProperty("id"), "2");
        document.addField(SolrStandardConfigurator.getFieldProperty("title"), "Second Document");
        document.addField(SolrStandardConfigurator.getFieldProperty("content"), "Hi, this is the second document");
        document.addField(SolrStandardConfigurator.getFieldProperty("date"), "2017-03-03T00:00:00Z");
        connector.insertDocument(document);
        docs.add(document.deepCopy());

        Stichwort regexStichwort = new RegExpStichwort("document");
        QueryResponse response = connector.query(regexStichwort.buildQuery(new ArrayList<>()));
        assertEquals(2, response.getResults().getNumFound());

        regexStichwort = new RegExpStichwort("doc");
        List<StichwortModifier> modifiers = new ArrayList<>();
        modifiers.add(StichwortModifier.PART_OF_WORD);
        response = connector.query(regexStichwort.buildQuery(modifiers));
        assertEquals(2, response.getResults().getNumFound());

        regexStichwort = new RegExpStichwort("second");
        response = connector.query(regexStichwort.buildQuery(new ArrayList<>()));
        assertEquals(1, response.getResults().getNumFound());

        List<RegExpStichwort> stichworte = new ArrayList<>();
        stichworte.add(new RegExpStichwort("very"));
        stichworte.add(new RegExpStichwort("second"));
        Variable<RegExpStichwort> variable = new Variable<>("Test Variable", stichworte);
        modifiers = new ArrayList<>();
        response = connector.query(variable.buildQuery(modifiers));
        assertEquals(2, response.getResults().getNumFound());

        stichworte = new ArrayList<>();
        stichworte.add(new RegExpStichwort("none"));
        stichworte.add(new RegExpStichwort("second"));
        variable = new Variable<>("Test Variable", stichworte);
        response = connector.query(variable.buildQuery(modifiers));
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
        gitProps.entrySet().stream().filter(map -> systemProperties.getProperty(map.getKey().toString()) == null)
            .forEach(map -> missingProps.append(map.getKey().toString()).append(","));
        assertTrue("Missing properties " + missingProps.toString(), missingProps.length() == 0);
    }

    @Test
    public void testAddAWord() {
        SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getTestUrl(),
                systemProperties.getProperty("solr.connection.testDb"));
        String testWord = "test";
        File file = SolrStandardConfigurator.getCompoundedWordsFile(systemProperties.getProperty("solr.connection.testDb"));
        assertTrue("No compoundedWords File", file.isFile());
        assertTrue("CompoundedWords File is not readable", file.canRead());
        assertTrue("CompoundedWords File is not writable", file.canWrite());
        connector.addToWordlist(testWord);

        Set<String> words = new HashSet<>();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String singleWord = currentLine.replace("\n", "").replace(";", "");
                words.add(singleWord);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Testword is not in List", words.contains(testWord));
        words.remove(testWord);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            for (String writableWords : words) {
                writer.write(writableWords + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("File has to be deletable", file.delete());
    }
}