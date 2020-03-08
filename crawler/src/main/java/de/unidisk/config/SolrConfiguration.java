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

    private static Map<String, String> fieldProperties;

    String server;
    int port;
    int rowLimit;
    String core;

    public SolrConfiguration(String server, int port, int rowLimit, String core) {
        this.server = server;
        this.port = port;
        this.rowLimit = rowLimit;
        this.core = core;
    }

    public static SolrConfiguration getInstance(){
        return SystemConfiguration.getInstance().getSolrConfiguration();
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

    public String[] getStandardFields() {
        return new String[]{getFieldProperty("id"),
                getFieldProperty("title"),
                getFieldProperty("date"),
                getFieldProperty("score")
        };
    }


    public String getFieldProperty(String field) {
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

    public void configureSolrQuery(SolrQuery query)  {
        query.set("indent", "true");
        query.set("rows", getRowLimit());
        query.setFields(getStandardFields());
        query.set("wt", "json");
    }

    public String getUrl(){
        return "http://" + getServer() + ":" + getPort() + "/solr/" + getCore();
    }

}
