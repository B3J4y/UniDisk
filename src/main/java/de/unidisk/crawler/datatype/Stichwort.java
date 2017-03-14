package de.unidisk.crawler.datatype;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.nlp.basics.EnhancedWithRegExp;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * Created by carl on 12.03.17.
 */
public class Stichwort extends EnhancedWithRegExp {
    private String name;

    public Stichwort(String name) {
        this.name = name;
    }

    public String buildRegExp() {
        return buildRegExp(name);
    }

    public SolrQuery buildQuery() {
        String regexp = buildRegExp(name);

        //check if there are at begin and end "/" for regexp support in queries
        if (!regexp.startsWith("/")) {
            regexp = "/" + regexp;
        }
        if (!regexp.endsWith("/")) {
            regexp += "/";
        }
        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperties().get("content") + ":" + regexp);
        solrQuery.set("indent", "true");
        solrQuery.set("rows", SolrStandardConfigurator.getLimit());
        solrQuery.setFields(SolrStandardConfigurator.getStandardFields());
        solrQuery.set("wt", "json");
        return solrQuery;
    }
}
