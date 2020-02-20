package de.unidisk.solr;

import de.unidisk.common.StichwortModifier;
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
    @Disabled
    void testConnectorWithRegex() throws Exception {
        SolrConnector connector = new SolrConnector(SolrConfiguration.getTestUrl());
        List<SolrInputDocument> docs = new ArrayList<>();
        SolrInputDocument document = new SolrInputDocument();
        document.addField(SolrConfiguration.getFieldProperty("id"), "1");
        document.addField(SolrConfiguration.getFieldProperty("title"), "Complex Multimedia Application Architectures");
        document.addField(SolrConfiguration.getFieldProperty("content"), "Today’s multimedia research is not limited to input-output and transfer " +
                "mechanisms anymore. Taking into account the ubiquitous networks such " +
                "architectures play an important role. The chair works on interoperability of " +
                "heterogeneous systems in today’s IT landscape. In the area of multimedial " +
                "applications those linked to learning and teaching are of foremost interest to us.");
        document.addField(SolrConfiguration.getFieldProperty("date"), new Date());
        connector.insertDocument(document);
        docs.add(document.deepCopy());
        document = new SolrInputDocument();
        document.addField(SolrConfiguration.getFieldProperty("id"), "2");
        document.addField(SolrConfiguration.getFieldProperty("title"), "Dependable and Energy Efficient Sensor Networks");
        document.addField(SolrConfiguration.getFieldProperty("content"), "Our research is focussed on dependable systems. We are especially interested " +
                "in diagnostic self-test in the field for processors, fault-tolerance techniques for " +
                "processors, middleware for reliable sensor nodes, and architectural synthesis for " +
                "processors.");
        document.addField(SolrConfiguration.getFieldProperty("date"), "2017-03-03T00:00:00Z");
        connector.insertDocument(document);
        docs.add(document.deepCopy());
        List<StichwortModifier> mods = new ArrayList<>();
        mods.add(StichwortModifier.NOT_CASE_SENSITIVE);
        mods.add(StichwortModifier.PART_OF_WORD);

        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("processors");
        SolrQuery query = SolrQueryConversion.buildWordQuery(keywords);
        QueryResponse response = connector.query(query);

        assertEquals(1, response.getResults().getNumFound());

        keywords.add("multimedia");
        query = SolrQueryConversion.buildWordQuery(keywords);
        response = connector.query(query);

        assertEquals(2, response.getResults().getNumFound());
        for (SolrInputDocument doc : docs) {
            connector.deleteDocument(doc);
        }
    }

    @Test
    void testKeywordconversion() {
        List<String> keywords = new ArrayList<>();
        keywords.add("processors");
        keywords.add("media");
        SolrQuery query = SolrQueryConversion.buildWordQuery(keywords);

        assertEquals("content:(processors media)", query.getQuery(), "Query is not right");
    }
}
