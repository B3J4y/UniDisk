package de.unidisk.crawler.datatype;

import de.unidisk.solr.nlp.basics.StichwortModifier;

import java.util.List;

/**
 * Created by carl on 24.03.17.
 */
public class SolrStichwort extends AbstractStichwort {

    public SolrStichwort(String name) {
        super(name);
    }

    @Override
    public String buildExpression(List<StichwortModifier> modifiers) {
        StringBuilder expressionBuilder = new StringBuilder();
        expressionBuilder.append(getName());
        return expressionBuilder.toString();
    }

    @Override
    public String getBegin() {
        return "(";
    }

    @Override
    public String getEnd() {
        return ")";
    }

    @Override
    public String getSeparator() {
        return " ";
    }
}
