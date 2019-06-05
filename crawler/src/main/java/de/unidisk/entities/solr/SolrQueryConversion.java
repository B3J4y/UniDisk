package de.unidisk.entities.solr;

import de.unidisk.common.StichwortModifier;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;
import java.util.StringJoiner;

public class SolrQueryConversion {

    public static SolrQuery buildWordQuery(List<String> keywords) {
        String expression = "("+ String.join(" ", keywords) + ")";
        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperty("content") + ":" + expression);
        SolrStandardConfigurator.configureSolrQuery(solrQuery);
        return solrQuery;
    }

    public static SolrQuery buildRegexQuery(List<ModifiedKeyword> keywords){
        StringJoiner expToJoin = new StringJoiner("|");
        for (ModifiedKeyword modifiedKeyword : keywords) {
            expToJoin.add(buildExpression(modifiedKeyword));
        }
        String expression = expToJoin.toString();
        //check if there are at begin and end "/" for regexp support in queries
        if (!expression.startsWith("/")) {
            expression = "/" + expression;
        }
        if (!expression.endsWith("/")) {
            expression += "/";
        }
        System.out.println(expression);
        SolrQuery solrQuery = new SolrQuery(SolrStandardConfigurator.getFieldProperty("content") + ":" + expression);
        SolrStandardConfigurator.configureSolrQuery(solrQuery);
        return solrQuery;
    }

    private static String buildExpression(ModifiedKeyword modifiedKeyword) {
        StringBuilder resultString = new StringBuilder();
        List<StichwortModifier> modifiers = modifiedKeyword.getModifier();
        if (modifiers.contains(StichwortModifier.START_OF_WORD)) {
            resultString.append("^[^a-zA-Z]*");
        } else {
            if (modifiers.contains(StichwortModifier.PART_OF_WORD)) {
                resultString.append("[a-zA-Z]*");
            }
        }
        String resultName = modifiedKeyword.getKeyword();
        if (modifiers.contains(StichwortModifier.NOT_CASE_SENSITIVE)) {
            resultName = "[" + modifiedKeyword.getKeyword().substring(0, 1).toLowerCase() +
                    modifiedKeyword.getKeyword().substring(0, 1).toUpperCase()
                    + "]" + modifiedKeyword.getKeyword().substring(1);
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
}
