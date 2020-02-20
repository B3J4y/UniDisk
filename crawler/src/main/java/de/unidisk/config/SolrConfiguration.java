package de.unidisk.config;

import org.apache.solr.client.solrj.SolrQuery;

import java.util.HashMap;
import java.util.Map;

public class SolrConfiguration {
    public static final String SERVER_NAME = "localhost";
    public static final String PORT = "8984";
    public static final String CORE = "unidisk";
    private static Map<String, String> fieldProperties;
    private static final int limit = 1000000;

    public static String[] getStandardFields() {
        return new String[]{getFieldProperty("id"),
                getFieldProperty("title"),
                getFieldProperty("date"),
                getFieldProperty("score")
        };
    }

    public static String getTestUrl() {
        return "http://" + SolrConfiguration.SERVER_NAME + ":" + SolrConfiguration.PORT + "/solr/" + SolrConfiguration.CORE;
    }

    public static String getFieldProperty(String field) {
        if (fieldProperties == null) {
            fieldProperties = new HashMap<>();
            fieldProperties.put("id", "id");
            fieldProperties.put("title", "title_de");
            fieldProperties.put("content", "content");
            fieldProperties.put("date", "date_dt");
            fieldProperties.put("score", "score");
        }
        return fieldProperties.get(field);
    }

    public static void configureSolrQuery(SolrQuery query) {
        query.set("indent", "true");
        query.set("rows", limit);
        query.setFields(getStandardFields());
        query.set("wt", "json");
    }
}
