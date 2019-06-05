package de.unidisk.crawler.datatype;

import de.unidisk.common.StichwortModifier;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

/**
 * Created by carl on 12.03.17.
 */
public interface Stichwort {
    String buildExpression(List<StichwortModifier> modifiers);

    SolrQuery buildQuery(List<StichwortModifier> modifiers);

    String getBegin();

    String getEnd();

    String getSeparator();

}
