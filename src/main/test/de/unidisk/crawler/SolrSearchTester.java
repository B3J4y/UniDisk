package de.unidisk.crawler;

import de.unidisk.crawler.datatype.SolrFile;
import de.unidisk.crawler.datatype.StichwortModifier;
import de.unidisk.crawler.datatype.Variable;
import de.unidisk.crawler.io.FilesToSolrConverter;
import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.nlp.datatype.RegExpStichwort;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

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
        Arrays.stream(files).filter(File::isDirectory).map(dir -> new Object[]{dir.getAbsolutePath(), dir.getName()})
                .forEach(result::add);
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
        assertEquals("Id from testfile", 1, solrFile.getSolrInputDocument().get(SolrStandardConfigurator.getFieldProperties("id")).getValue());
        assertEquals("Title of testfile", "FirstBook", solrFile.getSolrInputDocument().get(SolrStandardConfigurator.getFieldProperties("title")).getValue());
        assertEquals("Content from testfile", content, solrFile.getSolrInputDocument().get(SolrStandardConfigurator.getFieldProperties("content")).getValue());

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

        List<Variable<RegExpStichwort>> variables = new ArrayList<>();
        List<StichwortModifier> mods = new ArrayList<>();
        mods.add(StichwortModifier.NOT_CASE_SENSITIVE);
        mods.add(StichwortModifier.PART_OF_WORD);
        File testDir = new File(path);
        if (testDir == null && !testDir.isDirectory()) {
            return;
        }
        for (File file : testDir.listFiles()) {
            if (file.getName().endsWith(".ignore")) {
                logger.info("Found a config File: " + file.getName());
                BufferedReader bufferedReader;
                bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    String[] semicolonSplit = currentLine.split(";");
                    if (semicolonSplit.length != 2) {
                        logger.error("It was not an ignore File to config this test");
                        logger.error("currentLine: " + currentLine);
                        return;
                    }
                    String[] commaSplit = semicolonSplit[1].split(",");
                    List<RegExpStichwort> regExpStichworts = new ArrayList<>();
                    Arrays.stream(commaSplit).map(RegExpStichwort::new).forEach(regExpStichworts::add);
                    Variable<RegExpStichwort> variable = new Variable<>("FavDoc " + semicolonSplit[0], regExpStichworts);

                    assertTrue("Too little count of Stichworte",variable.getStichwortCount() >= 3);
                    variables.add(variable);
                }
            }
        }

        logger.debug(String.format("Got %d variables", variables.size()));

        //Checks if the regex results for single stichworte are the same as for stichworte bundled in variables
        for (Variable<RegExpStichwort> variable : variables) {
            Set<String> resultTitles = new HashSet<>();
            for (RegExpStichwort searchStichwort : variable.getStichworte()) {
                QueryResponse response = connector.query(searchStichwort.buildQuery(mods));
                SolrDocumentList results = response.getResults();
                if (results.getNumFound() <= 0) {
                    logger.debug("No Result for Stichwort " + searchStichwort);
                    continue;
                }

                for (int i = 0; i < results.getNumFound(); i++) {
                    resultTitles.add((String) results.get(i).getFieldValue(SolrStandardConfigurator.getFieldProperties("title")));
                }
                SolrDocument firstResult = results.get(0);
                String titleBestResult = (String) firstResult.getFieldValue(SolrStandardConfigurator.getFieldProperties("title"));
                logger.debug(String.format("Stichwort: %s; BestResult: %s, MaxScore: %f", searchStichwort, titleBestResult, results.getMaxScore()));
                assertEquals("RegExp Queries has usually a score of 1.0", Float.valueOf(1), results.getMaxScore());
            }
            QueryResponse response = connector.query(variable.buildQuery(mods));
            SolrDocumentList results = response.getResults();
            if (results.getNumFound() <= 0) {
                logger.debug("No Result for Variable " + variable.toString());
                continue;
            }
            for (int i = 0; i < results.getNumFound(); i++) {
                String resultTitle = (String) results.get(i).getFieldValue(SolrStandardConfigurator.getFieldProperties("title"));
                assertTrue("For the regex variable query is a title which couldn't found in Stichworte", resultTitles.contains(resultTitle));
                resultTitles.remove(resultTitle);
            }
            assertTrue("More titles found as stichwort than in variable", resultTitles.size() == 0);
        }
        for (SolrInputDocument document : documents) {
            connector.deleteDocument(document);
        }
        queryResponse = connector.queryAllDocuments();
        actualNumbers = queryResponse.getResults().getNumFound();
        assertEquals("No documents should be left in solr",
                0, actualNumbers);
    }
}