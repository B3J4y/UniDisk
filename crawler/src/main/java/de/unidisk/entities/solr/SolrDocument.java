package de.unidisk.entities.solr;

import org.apache.solr.client.solrj.beans.Field;

public class SolrDocument {
    @Field public String id;
    @Field("title_de") public String title;
    @Field public String content;
    @Field public String date;

    public SolrDocument(String id, String title, String content, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }
}
