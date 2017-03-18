package de.unidisk.crawler.datatype;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 12.03.17.
 */
public class Variable {
    private String name;
    private List<Stichwort> stichworte;

    public Variable(String name) {
        this.name = name;
        stichworte = new ArrayList<>();
    }

    public void addStichwort(Stichwort stichwort) {
        stichworte.add(stichwort);
    }

    public boolean hasStichwort(Stichwort stichwort) {
        return stichworte.contains(stichwort);
    }

    public List<Stichwort> getStichworte() {
        return stichworte;
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
        SolrStandardConfigurator.configureSolrQuery(solrQuery);
        return solrQuery;
    }

    public int getStichwortCount() {
        return stichworte.size();
    }

    @Override
    public String toString() {
        return name;
    }
}
