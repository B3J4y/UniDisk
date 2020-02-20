package de.unidisk.config;

public class CrawlerConfig {
    public static String[] whitelist = new String[]{
            "uni-potsdam.de"
    };

    /**
     * TODO dynamic import from universities in Germany liste
     */
    public static String[] seedList = new String[]{
            "https://uni-potsdam.de"
    };
    public static String storageLocation = "C:\\Users\\Tim Sauvageot\\Desktop\\crawledshit";
    public final static String collectionName = "mycollection";
    public final static String solrUrl = "http://localhost:8983/solr";

}
