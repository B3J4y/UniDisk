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

    @Field
    public int universityId;

    public CrawlDocument(String id, String url, String title, String content, int depth, Long datum, int universityId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.datum = datum;
        this.url = url;
        this.depth = depth;
        this.universityId = universityId;
    }

    public CrawlDocument(SolrDocument document){
        this.id = (String) document.get("id");
        this.title =  getProperty("title", document);
        this.content = getProperty("content",document);
        this.datum = getProperty("datum",document);
        this.url = getProperty("url",document);
        Long d = getProperty("depth",document);
        this.depth = d.intValue();
        this.universityId = ((Long) getProperty("universityId",document)).intValue();
    }

    private static <T> T getProperty(String field, SolrDocument document){
        return ((ArrayList<T>) document.get(field)).get(0);
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