package de.unidisk.crawler.model;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrDocument;

import java.util.ArrayList;

public class CrawlDocument {
    @Field
    public String id;

    @Field
    public String url;
    @Field
    public String name;

    @Field
    public String title;

    @Field
    public String content;

    @Field
    public Long datum;

    @Field
    public int depth;



    public CrawlDocument(String id, String url, String title, String content, int depth, Long datum) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.datum = datum;
        this.url = url;
        this.depth = depth;
    }

    public CrawlDocument(SolrDocument document){
        this.id = (String) document.get("id");
        this.title =  getProperty("title", document,"");
        this.content = getProperty("content",document,"");
        this.datum = getProperty("datum",document,0l);
        this.url = getProperty("url",document,"http://www.google.com");
        Long d = getProperty("depth",document,0l);
        this.depth = d.intValue();
    }

    private static <T> T getProperty(String field, SolrDocument document, T defaultValue){
        final Object fieldObject =  document.get(field);
        if(fieldObject == null)
            return defaultValue;
        return ((ArrayList<T>) fieldObject).get(0);
    }
    public static String[] getFields(){
        return new String[]{
                "id",
                "title",
                "content",
                "datum",
                "url",
                "depth",
                "universityId"
        };
    }
}