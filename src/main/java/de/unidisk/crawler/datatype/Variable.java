package de.unidisk.crawler.datatype;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

/**
 * Created by carl on 12.03.17.
 */
public class Variable<K extends Stichwort> {
    private String name;
    private List<K> stichworte;

    public Variable(String name, List<K> stichworte) {
        this.name = name;
        this.stichworte = stichworte;
    }

    public void addStichwort(K stichwort) {
        stichworte.add(stichwort);
    }

    public boolean hasStichwort(Stichwort stichwort) {
        return stichworte.contains(stichwort);
    }

    public List<K> getStichworte() {
        return stichworte;
    }

    public SolrQuery buildQuery(List<StichwortModifier> modifiers) {
        if (stichworte.size() == 0) {
            return null;
        }
        StringBuilder queryBuilder = new StringBuilder(stichworte.get(0).getBegin());

        for (Stichwort stichwort : stichworte) {
            queryBuilder.append(stichwort.buildExpression(modifiers));
            queryBuilder.append("|");
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(stichworte.get(0).getEnd());

        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperties("content") + ":" + queryBuilder.toString());
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
