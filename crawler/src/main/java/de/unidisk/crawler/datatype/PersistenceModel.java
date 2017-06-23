package de.unidisk.crawler.datatype;

import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by dehne on 24.02.2016.
 */
public interface PersistenceModel {
    void initStichFile(String filepath) throws IOException;

    void initVarMetaFile(String filepath) throws IOException;

    void stichwortVarToCsv(String filepath) throws IOException;

    void varMetaToCsv(String key, SolrDocumentList solrList, String filepath, String stich) throws IOException, URISyntaxException, InterruptedException;
}
