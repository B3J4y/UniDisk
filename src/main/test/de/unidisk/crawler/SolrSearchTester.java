package de.unidisk.crawler;

import de.unidisk.crawler.datatype.SolrFile;
import de.unidisk.crawler.io.FilesToSolrConverter;
import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This unit test is used to test all directories in {PROJECT_ROOT}/src/main/resources/test/solr with the solr connector.
 * Solr will index all files in each directory and all text searching algorithms can be tested in separate solr cores
 */
@RunWith(Parameterized.class)
public class SolrSearchTester {
    static private final Logger logger = LogManager.getLogger(SolrSearchTester.class);
    private String path;
    private String directory;
    String fileName = "1-FirstBook.txt";

    public SolrSearchTester(String path, String direcotry) {
        this.path = path;
        this.directory = direcotry;
    }

    //shows the name of the directory
    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> data() {
        File[] files = new File(String.join(File.separator, new String[]{".", "src", "main", "resources", "test", "solr"})).listFiles();
        Collection<Object[]> result = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                result.add(new Object[]{
                        file.getAbsolutePath(),
                        file.getName()
                });
            }
        }
        return result;
    }

    @After
    public void tearDown() throws Exception {
        SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getTestUrl());
        QueryResponse response = connector.queryAllDocuments();

        connector.deleteDocuments(response.getResults());

        File file = new File(path + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testFileCreation() throws Exception {
        String content = "Das hier ist das erste Testbuch. Hier steht der Inhalt drin.";
        String fileName = "1-FirstBook.txt";
        File file = new File(path + File.separator + fileName);
        assertTrue("File creation went wrong", file.createNewFile());
        PrintWriter out = new PrintWriter(file.getPath());
        out.println(content);
        out.close();

        SolrFile solrFile = new SolrFile(file.getAbsolutePath());
        Map<String, String> solrFields = SolrStandardConfigurator.getFieldProperties();
        assertEquals("Id from testfile", 1, solrFile.getSolrInputDocument().get(solrFields.get("id")).getValue());
        assertEquals("Title of testfile", "FirstBook", solrFile.getSolrInputDocument().get(solrFields.get("title")).getValue());
        assertEquals("Content from testfile", content, solrFile.getSolrInputDocument().get(solrFields.get("content")).getValue());

        assertTrue("File couldn't be deleted", file.delete());
    }

    @Test
    public void testWithDocuments() throws Exception {
        List<SolrInputDocument> documents = new FilesToSolrConverter(path).getSolrDocs();
        SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getTestUrl());
        if (documents.size() == 0) {
            logger.debug("Nothing to do here");
            return;
        }
        connector.insertDocuments(documents);
        QueryResponse queryResponse = connector.queryAllDocuments();
        long actualNumbers = queryResponse.getResults().getNumFound();
        assertEquals("Size of documents in directory differs to solr",
                Math.min(documents.size(), SolrStandardConfigurator.getLimit()), actualNumbers);
        for (SolrInputDocument document : documents) {
            connector.deleteDocument(document);
        }
        queryResponse = connector.queryAllDocuments();
        actualNumbers = queryResponse.getResults().getNumFound();
        assertEquals("No documents should be left in solr",
                0, actualNumbers);
    }
}