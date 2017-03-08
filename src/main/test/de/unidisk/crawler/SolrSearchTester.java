package de.unidisk.crawler;

import de.unidisk.crawler.datatype.SolrFile;
import de.unidisk.crawler.io.FilesToSolrConverter;
import de.unidisk.crawler.solr.SolrConnector;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This unit test is used to test all directories in {PROJECT_ROOT}/src/main/resources/test/solr with the solr connector.
 * Solr will index all files in each directory and all text searching algorithms can be tested in separate solr cores
 */
@RunWith(Parameterized.class)
public class SolrSearchTester {
    private String path;
    private String directory;

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
        assertEquals("Id from testfile", 1, solrFile.getSolrInputDocument().get("id").getValue());
        assertEquals("Title of testfile", "FirstBook", solrFile.getSolrInputDocument().get("name_ss").getValue());
        assertEquals("Content from testfile", content, solrFile.getSolrInputDocument().get("content").getValue());

        assertTrue("File couldn't be deleted", file.delete());
    }

    @Test
    public void testWithDocuments() throws Exception {
        List<SolrInputDocument> documents = new FilesToSolrConverter(path).getSolrDocs();
        SolrConnector connector = new SolrConnector(SolrConnector.getStandardUrl());
        for (SolrInputDocument document : documents) {
            connector.insertDocument(document);
        }
        QueryResponse queryResponse = connector.queryAllDocuments();
        long actualNumbers = queryResponse.getResults().getNumFound();
        assertEquals("Size of documents in directory differs to solr",
                Math.min(documents.size(), SolrConnector.getLimit()), actualNumbers);
        for (SolrInputDocument document : documents) {
            connector.deleteDocument(document);
        }
        queryResponse = connector.queryAllDocuments();
        actualNumbers = queryResponse.getResults().getNumFound();
        assertEquals("No docuemnts should be left in solr",
                0, actualNumbers);
    }
}