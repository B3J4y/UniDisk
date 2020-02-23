package de.unidisk.config;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import de.unidisk.solr.SolrApp;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.util.IOUtils;


import javax.faces.context.FacesContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SolrConfiguration {

    private static SolrConfiguration configuration;
    static private final Logger logger = LogManager.getLogger(SolrConfiguration.class.getName());

    public static SolrConfiguration Instance(){
        if(configuration == null)
            configuration = new SolrConfiguration();
        return configuration;
    }
    private static Map<String, String> fieldProperties;

    private static final String configJsonPath ="solr-config.json";

    private SolrConfig config;

    private SolrConfiguration() {
        /*try {
            InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("solr-config.json");

            String content = CharStreams.toString(new InputStreamReader(
                    is, Charsets.UTF_8));
            final Gson gson = new Gson();
            final SolrConfig config = gson.fromJson(content, SolrConfig.class);
            this.config = config;
        }catch (Exception e){
            logger.error("Unable to read solr config file.");
            SetupDefault();
        }*/
        SetupDefault();
        logger.info("Solr configuration: \n" + this.config.toString());
    }

    private void SetupDefault(){
        logger.info("Setting up default solr configuration.");
        this.config = Default();
    }

    private SolrConfig Default(){

        return new SolrConfig(
                "localhost",
                8984,
                10000,
                "unidisc"
        );
    }

    public static String[] getStandardFields() {
        return new String[]{getFieldProperty("id"),
                getFieldProperty("title"),
                getFieldProperty("date"),
                getFieldProperty("score")
        };
    }

    public static String getTestUrl() throws IOException {
        final SolrConfiguration config = SolrConfiguration.Instance();
        return "http://" + config.getServer() + ":" + config.getPort() + "/solr/" + config.getCore();
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

    public static void configureSolrQuery(SolrQuery query) throws IOException {
        final SolrConfiguration config = SolrConfiguration.Instance();
        query.set("indent", "true");
        query.set("rows", config.getRowLimit());
        query.setFields(getStandardFields());
        query.set("wt", "json");
    }

    public String getServer(){
        return this.config.getServer();
    }

    public String getCore(){
        return this.config.getCore();
    }

    public int getPort() {
        return this.config.getPort();
    }

    public int getRowLimit() {
        return this.config.getRowLimit();
    }

    private class SolrConfig{
        String server;
        int port;
        int rowLimit;
        String core;

        public SolrConfig(String server, int port, int rowLimit, String core) {
            this.server = server;
            this.port = port;
            this.rowLimit = rowLimit;
            this.core = core;
        }


        public SolrConfig() {
        }

        public String getServer() {
            return server;
        }

        public int getPort() {
            return port;
        }

        public int getRowLimit() {
            return rowLimit;
        }

        public String getCore() {
            return core;
        }

        @Override
        public String toString() {
            return "SolrConfig{" +
                    "server='" + server + '\'' +
                    ", port=" + port +
                    ", rowLimit=" + rowLimit +
                    ", core='" + core + '\'' +
                    '}';
        }
    }
}
