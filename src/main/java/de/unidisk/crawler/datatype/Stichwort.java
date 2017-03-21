package de.unidisk.crawler.datatype;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.nlp.basics.EnhancedWithRegExp;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.HashSet;
import java.util.List;

/**
 * Created by carl on 12.03.17.
 */
public class Stichwort extends EnhancedWithRegExp {
    private String name;

    public Stichwort(String name) {
        this.name = name;
    }

    public Stichwort(String name, List<Modifier> modifiers) {
        this.name = name;
        this.modifiers = new HashSet<>(modifiers);
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
        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperties("content") + ":" + regexp);
        SolrStandardConfigurator.configureSolrQuery(solrQuery);
        return solrQuery;
    }

    @Override
    public String toString() {
        return name;
    }
}
