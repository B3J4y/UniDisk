package de.unidisk.crawler.util;

import java.net.MalformedURLException;
import java.net.URL;

public  class DomainHelper {

    private DomainHelper(){}

    public static String getDomain(String urlString){
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return url.getHost();
    }
}
