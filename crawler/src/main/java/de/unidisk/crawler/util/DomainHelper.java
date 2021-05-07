package de.unidisk.crawler.util;

import java.net.URI;
import java.net.URISyntaxException;

public  class DomainHelper {

    private DomainHelper(){}

    public static String getDomain(String url){
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return url;
        }
        return uri.getHost();
    }
}
