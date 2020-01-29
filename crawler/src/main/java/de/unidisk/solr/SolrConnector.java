package de.unidisk.solr;

import de.unidisk.config.SolrConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.List;

public class SolrConnector {
    static private final Logger logger = LogManager.getLogger(SolrConnector.class.getName());
    private SolrClient client ;
    private final String serverUrl;
    private static int limit =1000000;

    public SolrConnector(String serverUrl) {
        logger.debug("Entering SolrConnector Constructor with serverUrl:" + serverUrl);
        this.serverUrl = serverUrl;
        client = new HttpSolrClient.Builder(serverUrl)
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

    public QueryResponse queryAllDocuments() throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(limit);
        return client.query(solrQuery);
    }

    public void insertDocument(SolrInputDocument document) throws IOException, SolrServerException {
        client.add(document);
        client.commit();
    }

    public void insertDocuments(List<SolrInputDocument> documents) throws IOException, SolrServerException {
        UpdateResponse updateResponse = client.add(documents);
        UpdateResponse updateCommit = client.commit();

    }

    public void deleteDocument(SolrInputDocument document) throws IOException, SolrServerException {
        client.deleteById(String.valueOf(document.get("id").getValue()));
        client.commit();
    }

    public void deleteDocuments(SolrDocumentList documents) throws IOException, SolrServerException {
        for (int i = 0; i < documents.getNumFound(); i++) {
            SolrDocument document = documents.get(i);
            String idIdentifier = SolrConfiguration.getFieldProperty("id");
            client.deleteById((String) document.getFieldValue(idIdentifier));
        }
        client.commit();
    }
/*
    public void addToWordlist(String word) {
        File file = SolrStandardConfigurator.getCompoundedWordsFile(dbName);
        if (file == null) {
            return;
        }
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
        if (!words.contains(word)) {
            words.add(word);
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                for (String writableWords : words) {
                    writer.write(writableWords + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

 */
    public String getServerUrl(){
        return serverUrl;
    }
}
