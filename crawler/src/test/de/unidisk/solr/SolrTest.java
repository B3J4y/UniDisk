package de.unidisk.solr;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.crawler.datatype.StichwortModifier;
import de.unidisk.crawler.datatype.Variable;
import de.unidisk.nlp.datatype.RegExpStichwort;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SolrTest {

    @Test
    public void smokeTest() throws CommunicationsException {
        SolrConnector connector = new SolrConnector(SolrConfiguration.getTestUrl());
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
        SolrConnector connector = new SolrConnector(SolrConfiguration.getTestUrl());
        List<SolrInputDocument> docs = new ArrayList<>();
        SolrInputDocument document = new SolrInputDocument();
        document.addField(SolrConfiguration.getFieldProperty("id"), "1");
        document.addField(SolrConfiguration.getFieldProperty("title"), "First Document");
        document.addField(SolrConfiguration.getFieldProperty("content"), "Hi, this is the very first document");
        document.addField(SolrConfiguration.getFieldProperty("date"), new Date());
        connector.insertDocument(document);
        docs.add(document.deepCopy());
        document = new SolrInputDocument();
        document.addField(SolrConfiguration.getFieldProperty("id"), "2");
        document.addField(SolrConfiguration.getFieldProperty("title"), "Second Document");
        document.addField(SolrConfiguration.getFieldProperty("content"), "Hi, this is the second document");
        document.addField(SolrConfiguration.getFieldProperty("date"), "2017-03-03T00:00:00Z");
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
}