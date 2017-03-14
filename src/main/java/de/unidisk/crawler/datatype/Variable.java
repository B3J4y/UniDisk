package de.unidisk.crawler.datatype;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 12.03.17.
 */
public class Variable {
    String name;
    List<Stichwort> stichworte;

    public Variable(String name) {
        this.name = name;
        stichworte = new ArrayList<>();
    }

    public void addStichwort(Stichwort stichwort) {
        stichworte.add(stichwort);
    }

    public SolrQuery buildQuery() {
        StringBuilder queryBuilder = new StringBuilder("/");

        for (Stichwort stichwort : stichworte) {
            queryBuilder.append(stichwort.buildRegExp());
            queryBuilder.append("|");
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append("/");

        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperties().get("content") + ":" + queryBuilder.toString());
        solrQuery.set("indent", "true");
        solrQuery.set("rows", SolrStandardConfigurator.getLimit());
        solrQuery.setFields(SolrStandardConfigurator.getStandardFields());
        solrQuery.set("wt", "json");

        return solrQuery;
    }
}
