package de.unidisk.solr;


import de.unidisk.config.SolrConfiguration;
import de.unidisk.entities.solr.SolrQueryConversion;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SolrQueryConversionTest {

    @Test
    void testKeywordconversion() {
        List<String> keywords = new ArrayList<>();
        keywords.add("processors");
        keywords.add("media");
        SolrQuery query = SolrQueryConversion.buildWordQuery(keywords);

        assertEquals("content:(processors media)", query.getQuery(), "Query is not right");
    }
}
