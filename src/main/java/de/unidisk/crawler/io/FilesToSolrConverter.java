package de.unidisk.crawler.io;

import de.unidisk.crawler.datatype.SolrFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts files to solr-documents
 */
public class FilesToSolrConverter {
    static private final Logger logger = LogManager.getLogger(FilesToSolrConverter.class.getName());
    private final String dirPath;

    public FilesToSolrConverter(String dirPath) throws IOException {
        if (new File(dirPath).isDirectory()) {
            this.dirPath = dirPath;
        } else {
            throw new IOException();
        }
    }

    public List<SolrInputDocument> getSolrDocs() {
        File directory = new File(dirPath);
        List<SolrInputDocument> solrInputDocuments = new ArrayList<>();
        for (File file : directory.listFiles()) {
            try {
                SolrFile solrFile = new SolrFile(file.getAbsolutePath());
                solrInputDocuments.add(solrFile.getSolrInputDocument());
            } catch (IOException e) {
                logger.error("Problems with " + file.getAbsolutePath() + ". So nothing is done with it.");
            }
        }
        return solrInputDocuments;
    }
}
