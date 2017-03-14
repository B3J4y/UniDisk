package de.unidisk.crawler.solr;

import de.unidisk.common.SystemProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by carl on 12.03.17.
 */
public class SolrStandardConfigurator {
    private static int limit =1000000;
    private static Properties systemProperties = SystemProperties.getInstance();
    private static Map<String, String> fieldProperties;

    static public int getLimit() {
        return limit;
    }

    public static String getStandardUrl() {
        return "http://" + systemProperties.getProperty("solr.connection.url") + ":" +
                systemProperties.getProperty("solr.connection.port")
                + "/solr/"
                + systemProperties.getProperty("solr.connection.db");
    }

    public static String getTestUrl() {
        return "http://" + systemProperties.getProperty("solr.connection.url") + ":" +
                systemProperties.getProperty("solr.connection.port")
                + "/solr/"
                + systemProperties.getProperty("solr.connection.testDb");
    }

    public static String[] getStandardFields() {
        return new String[]{getFieldProperties().get("id"),
                getFieldProperties().get("title"),
                getFieldProperties().get("date")
        };
    }

    public static Map<String, String> getFieldProperties() {
        if (fieldProperties == null) {
            fieldProperties = new HashMap<>();
            fieldProperties.put("id", "id");
            fieldProperties.put("title", "title_de");
            fieldProperties.put("content", "text_de");
            fieldProperties.put("date", "date_dt");
        }
        return fieldProperties;
    }
}
