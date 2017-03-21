package de.unidisk.nlp.datatype;

import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.crawler.datatype.StichwortModifier;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

/**
 * Created by carl on 21.03.17.
 */
public class RegExpStichwort implements Stichwort {
    private final String name;

    public RegExpStichwort(String name) {
        this.name = name;
    }

    @Override
    public String buildExpression(List<StichwortModifier> modifiers) {
        StringBuilder resultString = new StringBuilder();
        if (modifiers.contains(StichwortModifier.START_OF_WORD)) {
            resultString.append("^[^a-zA-Z]*");
        } else {
            if (modifiers.contains(StichwortModifier.PART_OF_WORD)) {
                resultString.append("[a-zA-Z]*");
            }
        }
        String resultName = name;
        if (modifiers.contains(StichwortModifier.NOT_CASE_SENSITIVE)) {
            resultName = "[" + name.substring(0, 1).toLowerCase() + name.substring(0, 1).toUpperCase() + "]" + name.substring(1);
        }
        resultString.append(resultName);
        if (modifiers.contains(StichwortModifier.END_OF_WORD)) {
            resultString.append("[^a-zA-Z]*$");
        } else {
            if (modifiers.contains(StichwortModifier.PART_OF_WORD)) {
                resultString.append("[a-zA-Z]*");
            }
        }
        return resultString.toString();
    }

    @Override
    public SolrQuery buildQuery(List<StichwortModifier> modifiers){
        String regexp = buildExpression(modifiers);

        //check if there are at begin and end "/" for regexp support in queries
        if (!regexp.startsWith(getBegin())) {
            regexp = getBegin() + regexp;
        }
        if (!regexp.endsWith(getEnd())) {
            regexp += getEnd();
        }
        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperties("content") + ":" + regexp);
        SolrStandardConfigurator.configureSolrQuery(solrQuery);
        return solrQuery;
    }

    @Override
    public String getBegin() {
        return "/";
    }

    @Override
    public String getEnd() {
        return "/";
    }

    @Override
    public String toString() {
        return name;
    }
}
