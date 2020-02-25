package de.unidisk.crawler.solr;

import de.unidisk.config.SolrConfiguration;
import de.unidisk.crawler.model.CrawlDocument;
import org.apache.solr.client.solrj.SolrQuery;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carl on 12.03.17.
 */
public class SolrStandardConfigurator {
    private static int limit =1000000;
    private static Map<String, String> fieldProperties;

    static public int getLimit() {
        return limit;
    }

    public static String getStandardUrl() {
        return getTestUrl();
    }

    public static String getTestUrl() {
        final SolrConfiguration config = SolrConfiguration.Instance();
        return "http://" + config.getServer() + ":" + config.getPort() + "/solr/" + config.getCore();
    }

    public static String[] getStandardFields() {
        return new String[]{getFieldProperty("id"),
                getFieldProperty("title"),
                getFieldProperty("datum"),
                getFieldProperty("score")
        };
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

    public static File getCompoundedWordsFile(String solrDb) {
        String[] path = new String[]{"server", "solr", solrDb, "germanwords.txt"};
        //todo hier nochmal nachgucken
        //File compundedWordsFile = new File(systemProperties.getProperty("solr.system.path")
        //        + File.separator + String.join(File.separator, path));
        File compundedWordsFile = new File("germanword.txt");
        if (compundedWordsFile.isFile()) {
            return compundedWordsFile;
        }
        if (!compundedWordsFile.exists()) {
            try {
                if (compundedWordsFile.createNewFile()) {
                    return compundedWordsFile;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void configureSolrQuery(SolrQuery query) {
        query.set("indent", "true");
        query.set("rows", SolrStandardConfigurator.getLimit());
        query.setFields(CrawlDocument.getFields());
        query.set("wt", "json");
    }
}
