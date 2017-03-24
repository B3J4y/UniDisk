package de.unidisk.crawler.datatype;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

/**
 * Created by carl on 24.03.17.
 */
public abstract class AbstractStichwort implements Stichwort {
    private final String name;

    protected AbstractStichwort(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    @Override
    public SolrQuery buildQuery(List<StichwortModifier> modifiers){
        String expression = buildExpression(modifiers);

        //check if there are at begin and end "/" for regexp support in queries
        if (!expression.startsWith(getBegin())) {
            expression = getBegin() + expression;
        }
        if (!expression.endsWith(getEnd())) {
            expression += getEnd();
        }
        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperties("content") + ":" + expression);
        SolrStandardConfigurator.configureSolrQuery(solrQuery);
        return solrQuery;
    }

    @Override
    public String toString() {
        return name;
    }
}
