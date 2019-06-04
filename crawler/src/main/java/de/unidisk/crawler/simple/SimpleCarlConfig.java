package de.unidisk.crawler.simple;

public class SimpleCarlConfig {
    public static String[] whitelist = new String[]{
            "www.uni-potsdam.de"
    };

    /**
     * TODO dynamic import from universities in Germany liste
     */
    public static String[] seedList = new String[]{
            "https://uni-potsdam.de"
    };
    public static String crawledShitPlace = "C:\\Users\\dehne\\Desktop\\crawledshit";
}
